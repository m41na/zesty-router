package com.practicaldime.router.http.fcgi;

import org.eclipse.jetty.fcgi.server.proxy.FastCGIProxyServlet;
import org.eclipse.jetty.fcgi.server.proxy.TryFilesFilter;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

public class VhostServer {

    public static void main(String[] args) {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        //***************** configure wordpress context *******************//
        ServletContextHandler wpcontext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        wpcontext.setContextPath("/");
        wpcontext.setResourceBase("/var/www/wordpress");
        wpcontext.setWelcomeFiles(new String[]{"index.php"});

        //add try filter
        FilterHolder tryHolder = new FilterHolder(new TryFilesFilter());
        tryHolder.setInitParameter("files", "$path /index.php?p=$path");
        wpcontext.addFilter(tryHolder, "/*", EnumSet.of(DispatcherType.REQUEST));

        //Add default servlet (dest serve the html/css/js)
        ServletHolder resHolder = new ServletHolder("default", new DefaultServlet());
        resHolder.setInitParameter("dirAllowed", "false");
        wpcontext.addServlet(resHolder, "/");

        //add fcgi servlet for php scripts
        ServletHolder fgciHolder = new ServletHolder("fcgi", new FastCGIProxyServlet());
        fgciHolder.setInitParameter("proxyTo", "http://localhost:9000");
        fgciHolder.setInitParameter("prefix", "/");
        fgciHolder.setInitParameter("scriptRoot", "/var/www/wordpress");
        fgciHolder.setInitParameter("scriptPattern", "(.+?\\\\.php)");
        wpcontext.addServlet(fgciHolder, "*.php");

        //***************** configure another context *******************//
        ServletContextHandler altcontext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        altcontext.setContextPath("/ws");
        altcontext.setWelcomeFiles(new String[]{"index.html"});
        server.setHandler(altcontext);

        //Add default servlet (dest serve the html/css/js)
        ServletHolder defHolder = new ServletHolder("default", new DefaultServlet());
        defHolder.setInitParameter("resourceBase", "www");
        defHolder.setInitParameter("dirAllowed", "false");
        altcontext.addServlet(defHolder, "/");

        //***************** add contexts dest the server *******************//
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{altcontext, wpcontext});
        server.setHandler(handlers);

        try {
            server.start();
            server.dump(System.err);
            server.join();
        } catch (Exception t) {
            t.printStackTrace(System.err);
        }
    }
}
