package com.practicaldime.zesty.fcgi;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CGI;

public class CgiPyServer {

    public static void main(String[] args) {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setResourceBase("/var/www/pypress");
        context.setWelcomeFiles(new String[]{"index.html"});
        server.setHandler(context);
        
        //Add default servlet (to serve the html/css/js)
        ServletHolder defHolder = new ServletHolder("default",new DefaultServlet());
        defHolder.setInitParameter("dirAllowed","false");
        context.addServlet(defHolder,"/");
        
        //add fcgi servlet
        ServletHolder cgiHolder = new ServletHolder("cgi",new CGI());
        cgiHolder.setInitParameter("path","/bin:/usr/bin:/usr/local/bin");
        cgiHolder.setInitParameter("cgibinResourceBaseIsRelative","true");
        cgiHolder.setInitParameter("commandPrefix","/usr/bin/python");
        cgiHolder.setInitParameter("cgibinResourceBase","/cgi-bin");
        cgiHolder.setInitOrder(1);
        cgiHolder.setAsyncSupported(true);
        context.addServlet(cgiHolder,"/cgi-bin/*");

        try {
            server.start();
            server.dump(System.err);
            server.join();
        } catch (Exception t) {
            t.printStackTrace(System.err);
        }
    }
}
