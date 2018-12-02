package com.practicaldime.zesty.servlet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandlerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final String ID = UUID.randomUUID().toString();
    private final Logger LOG = LoggerFactory.getLogger(HandlerServlet.class);

    public void handle(HandlerRequest request, HandlerResponse response) {
        //override in subclasses to provide behavior
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //delegate to super implementation
        super.doTrace(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //delegate to super implementation
        super.doOptions(req, resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //delegate to super implementation
        super.doHead(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HandlerRequest request = (HandlerRequest) req;
        HandlerResponse response = (HandlerResponse) resp;
        doProcess(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HandlerRequest request = (HandlerRequest) req;
        HandlerResponse response = (HandlerResponse) resp;
        doProcess(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HandlerRequest request = (HandlerRequest) req;
        HandlerResponse response = (HandlerResponse) resp;
        doProcess(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HandlerRequest request = (HandlerRequest) req;
        HandlerResponse response = (HandlerResponse) resp;
        doProcess(request, response);
    }

    protected void doProcess(HandlerRequest request, HandlerResponse response) throws ServletException, IOException {
        //AsyncContext async = request.startAsync(request, response);
        
        try {
            //handle the request
            handle(request, response);
        } catch (Exception e) {
            LOG.error("Exception occured while executing 'handle()' function", e);
            response.sendError(500, e.getMessage());
            return;
        }
        
        LOG.info("Servlet {} handled request successfully. Now preparing response", ID);
        if (response.forward) {
            try {
                request.getRequestDispatcher(response.routeUri).forward(request, response);
            } catch (IOException | ServletException e) {
                LOG.error("Exception occured while executing 'handle()' function", e);
                response.sendError(500, e.getMessage());
            }
            return;
        }

        if (response.redirect) {
            response.sendRedirect(response.routeUri);
            return;
        }

        if (request.error) {
            response.sendError(HttpStatus.BAD_REQUEST_400, request.message());
            return;
        }

        if (!response.isCommitted()) {
            //prepare response
            ByteBuffer content = ByteBuffer.wrap(response.getContent());
            //write response
            try (WritableByteChannel out = Channels.newChannel(response.getOutputStream())) {
                out.write(content);
            }
        }
    }

    protected final Thread.UncaughtExceptionHandler h = (Thread th, Throwable ex) -> {
        LOG.error("Uncaught exception in thread : " + th.getName(), ex);
    };
}
