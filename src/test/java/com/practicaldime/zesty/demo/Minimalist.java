package com.practicaldime.zesty.demo;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Minimalist {

    public static void main0(String[] args) throws Exception {
        Server server = new Server(8080);
        //add handler
        server.setHandler(new HelloHandler());

        server.start();
        server.join();
    }

    public static void main1(String[] args) throws Exception {
        Server server = new Server(8080);
        //add handler
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        //add servlet
        ServletHolder holder = new ServletHolder(new HelloServlet());
        handler.addServlet(holder);

        server.start();
        server.join();
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        //add handler
        ServletContextHandler servlets = new ServletContextHandler();
        servlets.setContextPath("/");
        //add hello servlet
        ServletHolder holder = new ServletHolder(new HelloServlet());
        servlets.addServlet(holder, "/api/*");
        //mount another servlet
        ServletHolder home = new ServletHolder(new PubHomeServlet());
        servlets.addServlet(home, "/zes/*");
        //add default servlet
        addDefaultResourceHandlers(servlets);
        server.setHandler(servlets);

        server.start();
        server.join();
    }

    public static void main3(String[] args) throws Exception {
        Server server = new Server(8080);
        //add api handler
        ServletContextHandler servlets = new ServletContextHandler();
        servlets.setContextPath("/api");
        //add hello servlet
        ServletHolder holder = new ServletHolder(new HelloServlet());
        servlets.addServlet(holder, "/*");
        //add default servlet
        ServletContextHandler resources = createDefaultResourceHandler("/");
        //bundle up handlers
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{servlets, resources});
        //set handlers
        server.setHandler(handlers);

        server.start();
        server.join();
    }

    private static void addDefaultResourceHandlers(ServletContextHandler servlets) {
        // Configuration for serving /A/* from X/V/A
        DefaultServlet aServlet = new DefaultServlet();
        ServletHolder aHolder = new ServletHolder("default", DefaultServlet.class);
        aHolder.setInitParameter("resourceBase", "build/public");
        aHolder.setInitParameter("welcomeFile", "index.html");
        servlets.addServlet(aHolder, "/*");
    }

    private static ServletContextHandler createDefaultResourceHandler(String context) {
        DefaultServlet aServlet = new DefaultServlet();
        ServletHolder aHolder = new ServletHolder(aServlet);
        aHolder.setInitParameter("resourceBase", "docs/");
        aHolder.setInitParameter("welcomeFile", "index.html");
        ServletContextHandler handler = new ServletContextHandler(null, context);
        handler.addServlet(aHolder, "/*");
        return handler;
    }

    @SuppressWarnings("serial")
    public static class HelloServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            response.setContentType("text/html; charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("<h1>Hello from HelloServlet</h1>");
        }
    }

    public static class PubHomeServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            //request.newPushBuilder().path("favicon.ico").path("img/zesty.png"); //servlet-api 4
            request.getRequestDispatcher("index.html").forward(request, response);
        }
    }

    public static class HelloHandler extends AbstractHandler {
        final String greeting = "Hello from HelloHandler";

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setContentType("text/html; charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("<h1>" + greeting + "</h1>");
            baseRequest.setHandled(true);
        }
    }
}
