package com.practicaldime.zesty.app;

import com.practicaldime.zesty.basics.RouteHandle;
import com.practicaldime.zesty.servlet.HandlerConfig;
import com.practicaldime.zesty.servlet.HandlerFilter;
import com.practicaldime.zesty.servlet.HandlerFunction;
import com.practicaldime.zesty.servlet.HandlerServlet;
import com.practicaldime.zesty.sse.EventsEmitter;
import com.practicaldime.zesty.websock.AppWsPolicy;
import com.practicaldime.zesty.websock.AppWsProvider;

import javax.servlet.http.HttpServlet;

public interface Restful {

    String resolve(String path);

    // ************* FILTERS *****************//
    Restful filter(HandlerFilter filter);

    Restful filter(String context, HandlerFilter filter);

    // ************* SERVLETS *****************//
    Restful servlet(String path, HandlerConfig config, HttpServlet handler);

    // ************* GENERIC *****************//
    Restful route(String method, String path, HandlerServlet handler);

    Restful route(String method, String path, HandlerFunction handler);

    Restful route(String method, String path, HandlerConfig config, HandlerFunction handler);

    Restful route(String method, String path, HandlerConfig config, HandlerServlet handler);

    Restful route(String method, String path, String accept, String type, HandlerConfig config, HandlerFunction handler);

    Restful route(String method, String path, String accept, String type, HandlerConfig config, HandlerServlet handler);

    // ************* HEAD *****************//
    Restful head(String path, HandlerServlet handler);

    Restful head(String path, HandlerFunction handler);

    Restful head(String path, HandlerConfig config, HandlerFunction handler);

    Restful head(String path, HandlerConfig config, HandlerServlet handler);

    Restful head(String path, String accept, String type, HandlerConfig config, HandlerFunction handler);

    Restful head(String path, String accept, String type, HandlerConfig config, HandlerServlet handler);

    // ************* TRACE *****************//
    Restful trace(String path, HandlerServlet handler);

    Restful trace(String path, HandlerFunction handler);

    Restful trace(String path, HandlerConfig config, HandlerServlet handler);

    Restful trace(String path, HandlerConfig config, HandlerFunction handler);

    Restful trace(String path, String accept, String type, HandlerConfig config, HandlerFunction handler);

    Restful trace(String path, String accept, String type, HandlerConfig config, HandlerServlet handler);

    // ************* OPTIONS *****************//
    Restful options(String path, HandlerServlet handler);

    Restful options(String path, HandlerFunction handler);

    Restful options(String path, HandlerConfig config, HandlerServlet handler);

    Restful options(String path, HandlerConfig config, HandlerFunction handler);

    Restful options(String path, String accept, String type, HandlerConfig config, HandlerFunction handler);

    Restful options(String path, String accept, String type, HandlerConfig config, HandlerServlet handler);

    // ************* GET *****************//
    Restful get(String path, HandlerServlet handler);

    Restful get(String path, HandlerFunction handler);

    Restful get(String path, HandlerConfig config, HandlerServlet handler);

    Restful get(String path, HandlerConfig config, HandlerFunction handler);

    Restful get(String path, String accept, String type, HandlerConfig config, HandlerFunction handler);

    Restful get(String path, String accept, String type, HandlerConfig config, HandlerServlet handler);

    // ************* POST *****************//
    Restful post(String path, HandlerServlet handler);

    Restful post(String path, HandlerFunction handler);

    Restful post(String path, HandlerConfig config, HandlerServlet handler);

    Restful post(String path, HandlerConfig config, HandlerFunction handler);

    Restful post(String path, String accept, String type, HandlerConfig config, HandlerFunction handler);

    Restful post(String path, String accept, String type, HandlerConfig config, HandlerServlet handler);

    // ************* PUT *****************//
    Restful put(String path, HandlerServlet handler);

    Restful put(String path, HandlerFunction handler);

    Restful put(String path, HandlerConfig config, HandlerServlet handler);

    Restful put(String path, HandlerConfig config, HandlerFunction handler);

    Restful put(String path, String accept, String type, HandlerConfig config, HandlerFunction handler);

    Restful put(String path, String accept, String type, HandlerConfig config, HandlerServlet handler);

    // ************* DELETE *****************//
    Restful delete(String path, HandlerServlet handler);

    Restful delete(String path, HandlerFunction handler);

    Restful delete(String path, HandlerConfig config, HandlerServlet handler);

    Restful delete(String path, HandlerConfig config, HandlerFunction handler);

    Restful delete(String path, String accept, String type, HandlerConfig config, HandlerFunction handler);

    Restful delete(String path, String accept, String type, HandlerConfig config, HandlerServlet handler);

    // ************* ALL *****************//
    Restful all(String path, HandlerServlet handler);

    Restful all(String path, HandlerFunction handler);

    Restful all(String path, HandlerConfig config, HandlerServlet handler);

    Restful all(String path, HandlerConfig config, HandlerFunction handler);

    Restful all(String path, String accept, String type, HandlerConfig config, HandlerFunction handler);

    Restful all(String path, String accept, String type, HandlerConfig config, HandlerServlet handler);

    // ************* WEB-SOCKETS *****************//
    Restful websocket(String ctx, AppWsProvider provider);

    Restful websocket(String ctx, AppWsProvider provider, AppWsPolicy policy);

    // ************* SSE *****************//
    Restful subscribe(String path, HandlerConfig config, EventsEmitter eventsEmitter);

    // ************* Accept new Handler ************** //
    void accept(RouteHandle handle);
}
