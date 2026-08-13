package poc.jndi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Wraps a real H2 Connection so that DBConnectionWrapper.java (real,
 * unmodified erpjobs code) can keep calling
 * "alter session set time_zone='...'" whenever compTimeZone is non-empty.
 * That's valid Oracle syntax, but H2's parser has no ALTER SESSION grammar
 * at all - it only supports ALTER TABLE/USER/INDEX/SCHEMA/SEQUENCE/VIEW,
 * confirmed by H2's own error message, and no MODE=Oracle flag adds it
 * (MODE changes semantics of already-supported syntax, not the parser's
 * supported statement grammar).
 *
 * Obsidian's own "required" validation on the compTimeZone job parameter
 * won't allow a blank value, so DBConnectionWrapper will always attempt
 * this statement in practice - this proxy absorbs just that one
 * Oracle-only statement as a no-op and passes every other statement
 * (including the real "call SP_ERP_JOB_TEST_RUN()") through unchanged to
 * the real H2 connection.
 */
public class PocOracleCompatConnection {

    public static Connection wrap(Connection real) {
        return (Connection) Proxy.newProxyInstance(
                PocOracleCompatConnection.class.getClassLoader(),
                new Class<?>[] { Connection.class },
                new ConnectionHandler(real));
    }

    private static class ConnectionHandler implements InvocationHandler {
        private final Connection real;

        ConnectionHandler(Connection real) {
            this.real = real;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("createStatement".equals(method.getName()) && (args == null || args.length == 0)) {
                Statement realStmt = real.createStatement();
                return Proxy.newProxyInstance(
                        PocOracleCompatConnection.class.getClassLoader(),
                        new Class<?>[] { Statement.class },
                        new StatementHandler(realStmt));
            }
            return invokeReal(real, method, args);
        }
    }

    private static class StatementHandler implements InvocationHandler {
        private final Statement real;

        StatementHandler(Statement real) {
            this.real = real;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("execute".equals(method.getName()) && args != null && args.length >= 1
                    && args[0] instanceof String) {
                String sql = ((String) args[0]).trim().toLowerCase();
                if (sql.startsWith("alter session set time_zone")) {
                    return Boolean.FALSE; // no-op: H2 has no ALTER SESSION grammar
                }
            }
            return invokeReal(real, method, args);
        }
    }

    private static Object invokeReal(Object real, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(real, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}
