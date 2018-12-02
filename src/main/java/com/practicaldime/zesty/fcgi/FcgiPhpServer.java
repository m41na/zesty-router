package com.practicaldime.zesty.fcgi;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import org.eclipse.jetty.fcgi.server.proxy.FastCGIProxyServlet;
import org.eclipse.jetty.fcgi.server.proxy.TryFilesFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class FcgiPhpServer {

    public static void main(String[] args) {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setResourceBase("/var/www/wordpress");
        context.setWelcomeFiles(new String[]{"index.php"});
        server.setHandler(context);
        
        //add try filter
        FilterHolder tryHolder = new FilterHolder(new TryFilesFilter());
        tryHolder.setInitParameter("files", "$path /index.php?p=$path");
        context.addFilter(tryHolder, "/*", EnumSet.of(DispatcherType.REQUEST));
        
        //Add default servlet (to serve the html/css/js)
        ServletHolder defHolder = new ServletHolder("default",new DefaultServlet());
        defHolder.setInitParameter("dirAllowed","false");
        context.addServlet(defHolder,"/");
        
        //add fcgi servlet for php scripts
        ServletHolder fgciHolder = new ServletHolder("fcgi",new FastCGIProxyServlet());
        fgciHolder.setInitParameter("proxyTo","http://localhost:9000");
        fgciHolder.setInitParameter("prefix","/");
        fgciHolder.setInitParameter("scriptRoot","/var/www/wordpress");
        fgciHolder.setInitParameter("scriptPattern","(.+?\\\\.php)");
        context.addServlet(fgciHolder,"*.php");

        try {
            server.start();
            server.dump(System.err);
            server.join();
        } catch (Exception t) {
            t.printStackTrace(System.err);
        }
    }
}
