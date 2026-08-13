package poc.jndi;

import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;

/**
 * Minimal javax.naming.Context so DBConnectionWrapper's plain
 * "new InitialContext(); ic.lookup(name)" resolves to an in-process
 * DataSource without needing a real app server. Only lookup/bind do real
 * work; every other Context method is unused by ERPJobs' DB layer.
 */
public class PocContext implements Context {

    private static final Map<String, Object> BINDINGS = new ConcurrentHashMap<>();

    public static void register(String name, Object value) {
        BINDINGS.put(name, value);
    }

    @Override
    public Object lookup(String name) throws NamingException {
        Object value = BINDINGS.get(name);
        if (value == null) {
            throw new NameNotFoundException("No JNDI binding for '" + name
                    + "' - registered names: " + BINDINGS.keySet());
        }
        return value;
    }

    @Override
    public void bind(String name, Object obj) {
        BINDINGS.put(name, obj);
    }

    @Override
    public void rebind(String name, Object obj) {
        BINDINGS.put(name, obj);
    }

    @Override
    public void unbind(String name) {
        BINDINGS.remove(name);
    }

    @Override
    public void close() {
        // no-op
    }

    // --- everything below is unused by ERPJobs and intentionally unsupported ---

    @Override
    public Object lookup(Name name) throws NamingException {
        return lookup(name.toString());
    }

    @Override
    public void bind(Name name, Object obj) throws NamingException {
        throw unsupported("bind(Name)");
    }

    @Override
    public void rebind(Name name, Object obj) throws NamingException {
        throw unsupported("rebind(Name)");
    }

    @Override
    public void unbind(Name name) throws NamingException {
        throw unsupported("unbind(Name)");
    }

    @Override
    public void rename(Name oldName, Name newName) throws NamingException {
        throw unsupported("rename(Name,Name)");
    }

    @Override
    public void rename(String oldName, String newName) throws NamingException {
        throw unsupported("rename(String,String)");
    }

    @Override
    public NamingEnumeration<javax.naming.NameClassPair> list(Name name) throws NamingException {
        throw unsupported("list(Name)");
    }

    @Override
    public NamingEnumeration<javax.naming.NameClassPair> list(String name) throws NamingException {
        throw unsupported("list(String)");
    }

    @Override
    public NamingEnumeration<javax.naming.Binding> listBindings(Name name) throws NamingException {
        throw unsupported("listBindings(Name)");
    }

    @Override
    public NamingEnumeration<javax.naming.Binding> listBindings(String name) throws NamingException {
        throw unsupported("listBindings(String)");
    }

    @Override
    public void destroySubcontext(Name name) throws NamingException {
        throw unsupported("destroySubcontext(Name)");
    }

    @Override
    public void destroySubcontext(String name) throws NamingException {
        throw unsupported("destroySubcontext(String)");
    }

    @Override
    public Context createSubcontext(Name name) throws NamingException {
        throw unsupported("createSubcontext(Name)");
    }

    @Override
    public Context createSubcontext(String name) throws NamingException {
        throw unsupported("createSubcontext(String)");
    }

    @Override
    public Object lookupLink(Name name) throws NamingException {
        throw unsupported("lookupLink(Name)");
    }

    @Override
    public Object lookupLink(String name) throws NamingException {
        throw unsupported("lookupLink(String)");
    }

    @Override
    public NameParser getNameParser(Name name) throws NamingException {
        throw unsupported("getNameParser(Name)");
    }

    @Override
    public NameParser getNameParser(String name) throws NamingException {
        throw unsupported("getNameParser(String)");
    }

    @Override
    public Name composeName(Name name, Name prefix) throws NamingException {
        throw unsupported("composeName(Name,Name)");
    }

    @Override
    public String composeName(String name, String prefix) throws NamingException {
        throw unsupported("composeName(String,String)");
    }

    @Override
    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        throw unsupported("addToEnvironment");
    }

    @Override
    public Object removeFromEnvironment(String propName) throws NamingException {
        throw unsupported("removeFromEnvironment");
    }

    @Override
    public Hashtable<?, ?> getEnvironment() throws NamingException {
        return new Hashtable<Object, Object>();
    }

    @Override
    public String getNameInNamespace() throws NamingException {
        return "";
    }

    private static OperationNotSupportedException unsupported(String method) {
        return new OperationNotSupportedException("PocContext does not implement " + method
                + " - only lookup/bind are needed for this smoke test");
    }
}
