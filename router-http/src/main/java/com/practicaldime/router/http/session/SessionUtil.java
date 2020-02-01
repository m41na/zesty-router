package com.practicaldime.router.http.session;

import org.eclipse.jetty.server.session.*;

import java.io.File;

public class SessionUtil {

    public static SessionHandler fileSessionHandler() {
        SessionHandler sessionHandler = new SessionHandler();
        SessionCache sessionCache = new DefaultSessionCache(sessionHandler);
        sessionCache.setSessionDataStore(fileSessionDataStore());
        sessionHandler.setSessionCache(sessionCache);
        sessionHandler.setHttpOnly(true);
        // make additional changes to your SessionHandler here
        return sessionHandler;
    }

    public static FileSessionDataStore fileSessionDataStore() {
        FileSessionDataStore fileSessionDataStore = new FileSessionDataStore();
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        File storeDir = new File(baseDir, "javalin-session-store");
        storeDir.mkdir();
        fileSessionDataStore.setStoreDir(storeDir);
        return fileSessionDataStore;
    }

    public static SessionHandler sqlSessionHandler(String driver, String url) {
        SessionHandler sessionHandler = new SessionHandler();
        SessionCache sessionCache = new NullSessionCache(sessionHandler);
        sessionCache.setSessionDataStore(
                jdbcDataStoreFactory(driver, url).getSessionDataStore(sessionHandler)
        );
        sessionHandler.setSessionCache(sessionCache);
        sessionHandler.setHttpOnly(true);
        // make additional changes to your SessionHandler here
        return sessionHandler;
    }

    public static JDBCSessionDataStoreFactory jdbcDataStoreFactory(String driver, String url) {
        DatabaseAdaptor databaseAdaptor = new DatabaseAdaptor();
        databaseAdaptor.setDriverInfo(driver, url);
        JDBCSessionDataStoreFactory jdbcSessionDataStoreFactory = new JDBCSessionDataStoreFactory();
        jdbcSessionDataStoreFactory.setDatabaseAdaptor(databaseAdaptor);
        return jdbcSessionDataStoreFactory;
    }
}
