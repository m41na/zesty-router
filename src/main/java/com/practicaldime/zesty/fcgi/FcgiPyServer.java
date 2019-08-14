package com.practicaldime.zesty.fcgi;

import org.eclipse.jetty.fcgi.server.proxy.FastCGIProxyServlet;
import org.eclipse.jetty.fcgi.server.proxy.TryFilesFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

public class FcgiPyServer {

    public static void main(String[] args) {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setResourceBase("src/test/resources/fcgi");
        context.setWelcomeFiles(new String[]{"index.html"});
        server.setHandler(context);
        
        //Add default servlet (dest serve the html/css/js)
        ServletHolder defHolder = new ServletHolder("default",new DefaultServlet());
        defHolder.setInitParameter("dirAllowed","false");
        context.addServlet(defHolder,"/");
        
        //add try filter
        FilterHolder tryHolder = new FilterHolder(new TryFilesFilter());
        tryHolder.setInitParameter("files", "$path /index.php?p=$path");
        context.addFilter(tryHolder, "/*", EnumSet.of(DispatcherType.REQUEST));
        
        //add fcgi servlet
        ServletHolder fcgiHolder = new ServletHolder("vcgi",new FastCGIProxyServlet());
        fcgiHolder.setInitParameter("proxyTo","http://localhost:9000");
        fcgiHolder.setInitParameter("prefix","/");
        fcgiHolder.setInitParameter("scriptRoot","src/test/resources/fcgi");
        fcgiHolder.setInitParameter("scriptPattern","(.+?\\\\.py)");
        context.addServlet(fcgiHolder,"*.py");

        try {
            server.start();
            server.dump(System.err);
            server.join();
        } catch (Exception t) {
            t.printStackTrace(System.err);
        }
    }
}
