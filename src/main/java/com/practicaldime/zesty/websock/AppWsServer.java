package com.practicaldime.zesty.websock;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class AppWsServer {

    public static void main(String[] args) {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        
        //Add default servlet (to serve the html/css/js)
        ServletHolder defHolder = new ServletHolder("default",new DefaultServlet());
        defHolder.setInitParameter("resourceBase","www/wsock2");
        defHolder.setInitParameter("dirAllowed","true");
        context.addServlet(defHolder,"/");

        // Add a websocket to a specific path spec
        ServletHolder holderEvents = new ServletHolder("ws-events", new AppWsServlet(AppWsEvents::new));
        context.addServlet(holderEvents, "/events/*");

        try {
            server.start();
            server.dump(System.err);
            server.join();
        } catch (Exception t) {
            t.printStackTrace(System.err);
        }
    }
}
