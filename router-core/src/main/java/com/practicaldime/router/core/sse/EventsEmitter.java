package com.practicaldime.router.core.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.servlets.EventSource;

public interface EventsEmitter {

    void onOpen(ObjectMapper mapper, EventSource.Emitter emitter);
}
