package poc.jndi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.spi.InitialContextFactory;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;

/**
 * Activated via -Djava.naming.factory.initial=poc.jndi.PocInitialContextFactory
 * so DBConnectionWrapper's "new InitialContext()" returns a PocContext
 * instead of failing (silently, per its empty catch block).
 *
 * Running inside the real Obsidian/Jetty webapp (unlike the original
 * standalone smoke test) there is no driver code to call
 * PocContext.register() explicitly, so this factory self-registers the
 * jdbc/globus_test binding the first time it's loaded.
 *
 * AUTO_SERVER=TRUE is added here (not present in the original smoke-test
 * URL) so this H2 file can stay reachable from H2 Console/other tools at
 * the same time as the running Obsidian job - same technique Obsidian's
 * own DB already uses (jdbc:h2:~/obsidian;...;AUTO_SERVER=TRUE).
 */
public class PocInitialContextFactory implements InitialContextFactory {

    private static final String JNDI_NAME = "jdbc/globus_test";
    private static final String H2_URL =
            "jdbc:h2:file:C:/Workspace/erpjobs-h2-poc/data/erpjobs_poc;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE";

    static {
        try {
            JdbcDataSource realDs = new JdbcDataSource();
            realDs.setURL(H2_URL);
            realDs.setUser("sa");
            realDs.setPassword("");
            PocContext.register(JNDI_NAME, wrapDataSource(realDs));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Context getInitialContext(Hashtable<?, ?> environment) {
        return new PocContext();
    }

    /**
     * Wraps the DataSource so every Connection it hands out goes through
     * PocOracleCompatConnection - needed so GmDBManager.getConnection()
     * (real, unmodified DBConnectionWrapper.java) can call
     * "alter session set time_zone=..." without H2 rejecting it.
     */
    private static DataSource wrapDataSource(final DataSource real) {
        return (DataSource) Proxy.newProxyInstance(
                PocInitialContextFactory.class.getClassLoader(),
                new Class<?>[] { DataSource.class },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if ("getConnection".equals(method.getName()) && (args == null || args.length == 0)) {
                            Connection realConn = real.getConnection();
                            return PocOracleCompatConnection.wrap(realConn);
                        }
                        try {
                            return method.invoke(real, args);
                        } catch (InvocationTargetException e) {
                            throw e.getTargetException();
                        }
                    }
                });
    }
}
