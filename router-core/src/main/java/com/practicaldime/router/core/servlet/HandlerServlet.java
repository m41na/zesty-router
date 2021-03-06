package com.practicaldime.router.core.servlet;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public class HandlerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final String ID = UUID.randomUUID().toString();
    private final Logger LOG = LoggerFactory.getLogger(HandlerServlet.class);

    public void handle(HandlerRequest request, HandlerResponse response, HandlerPromise promise) throws Exception {
        //override in subclasses to handle request/response
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //delegate dest super implementation
        super.doTrace(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //delegate dest super implementation
        super.doOptions(req, resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //delegate dest super implementation
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

    protected void doProcess(HandlerRequest request, HandlerResponse response) {
        if (request.isAsyncSupported()) {
            final AsyncContext async = request.startAsync();
            //TODO: async.setTimeout(10000L); //can this value be externally configured?
            async.start(() -> {
                LOG.debug("STARTED ASYNC OPERATION");
                HandlerPromise promise = new HandlerPromise();
                promise.OnSuccess(new Function<HandlerResult, HandlerResult>() {
                    @Override
                    public HandlerResult apply(HandlerResult res) {
                        try {
                            LOG.info("Servlet {} request resolved with success status. Now preparing response", ID);
                            if (response.forward) {
                                try {
                                    request.getRequestDispatcher(response.routeUri).forward(request, response);
                                } catch (IOException | ServletException e) {
                                    LOG.error("Exception occurred while executing 'handle()' function", e);
                                    response.sendError(500, e.getMessage());
                                }
                            }

                            if (response.redirect) {
                                response.sendRedirect(response.routeUri);
                            }

                            if (request.error) {
                                response.sendError(HttpStatus.BAD_REQUEST_400, request.message());
                            }

                            if (!response.isCommitted()) {
                                //prepare response
                                ByteBuffer content = ByteBuffer.wrap(response.getContent());
                                //write response
                                try (WritableByteChannel out = Channels.newChannel(response.getOutputStream())) {
                                    out.write(content);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace(System.err);
                        } finally {
                            LOG.info("Async request '{}' COMPLETED successfully: {}", request.getRequestURI(), res);
                            async.complete();
                            LOG.info("Duration of processed request -> {} ms", res.updateStatus(Boolean.TRUE));
                            return res;
                        }
                    }
                });

                promise.OnFailure(new BiFunction<HandlerResult, Throwable, HandlerResult>() {
                    @Override
                    public HandlerResult apply(HandlerResult res, Throwable th) {
                        try {
                            LOG.info("Servlet {} request resolved with failure status. Now preparing response", ID);
                            int status = 500;
                            if (HandlerException.class.isAssignableFrom(th.getClass())) {
                                status = ((HandlerException) th).status;
                            }
                            response.sendError(status, th.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace(System.err);
                        } finally {
                            LOG.info("Async request '{}' COMPLETED with an exception", request.getRequestURI());
                            async.complete();
                            LOG.info("Duration of processed request -> {} ms", res.updateStatus(Boolean.FALSE));
                            return res;
                        }
                    }
                });

                //handle request
                try {
                    LOG.debug("DELEGATING REQUEST TO HANDLER METHOD WITH COMPLETION PROMISE");
                    handle(request, response, promise);
                } catch (Exception e) {
                    LOG.warn("Uncaught Exception in the promise resolver. Completing promise with failure: {}", e.getMessage());
                    e.printStackTrace(System.err);
                    promise.resolve(CompletableFuture.failedFuture(e));
                }
            });
            LOG.debug("RETURNING THREAD TO AWAIT ASYNC COMPLETION");
            LOG.info("ASYNC MODE> Request handling started in async context");
        } else {
            LOG.warn("*****ASYNC NOT SUPPORTED. You might need to handle writing the response in your servlet handler*****");
            HandlerPromise promise = new HandlerPromise();
            try {
                handle(request, response, promise);
            } catch (Exception e) {
                LOG.warn("Uncaught Exception in the promise resolver. Completing promise with failure: {}", e.getMessage());
                e.printStackTrace(System.err);
                promise.resolve(CompletableFuture.failedFuture(e));
            }
        }
    }
}
