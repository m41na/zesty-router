package com.practicaldime.zesty.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.servlets.EventSource;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public abstract class AppEventSource implements EventSource {

    protected final HttpServletRequest request;
    protected final ObjectMapper mapper;
    protected Emitter emitter;

    public AppEventSource(HttpServletRequest request, ObjectMapper mapper) {
        this.request = request;
        this.mapper = mapper;
    }

    @Override
    public void onOpen(Emitter emitter) throws IOException {
        this.emitter = emitter;
    }

    @Override
    public void onClose() {
        this.emitter.close();;
        this.emitter = null;
    }
}
