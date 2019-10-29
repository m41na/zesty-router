package com.practicaldime.zesty.sse;

import org.eclipse.jetty.servlets.EventSource;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public abstract class AppEventSource implements EventSource {

    protected final HttpServletRequest request;
    protected Emitter emitter;

    public AppEventSource(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void onOpen(Emitter emitter) throws IOException {
        this.emitter = emitter;
    }

    @Override
    public void onClose() {
        this.emitter.close();
        this.emitter = null;
    }
}
