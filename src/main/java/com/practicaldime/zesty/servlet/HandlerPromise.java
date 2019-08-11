package com.practicaldime.zesty.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HandlerPromise<R> {

    private static final Logger LOG = LoggerFactory.getLogger(HandlerPromise.class);
    private Function<R, CompletableFuture<HandlerResult>> success;
    private Function<Throwable, HandlerResult> failure;

    public HandlerResult resolve(CompletableFuture<R> action) {
        LOG.info("Start: resolve promise");
        return action.thenCompose(r -> success.apply(r))
                .exceptionally(th -> {
                    if(HandlerException.class.isAssignableFrom(th.getCause().getClass())){
                        return failure.apply(th.getCause());
                    }
                    else {
                        return failure.apply(new HandlerException(500, th.getCause().getMessage(), th));
                    }
                }).join();
    }

    public HandlerResult complete() {
        LOG.info("Start: complete promise");
        return success.apply(null)
                .exceptionally(th -> {
                    if(HandlerException.class.isAssignableFrom(th.getCause().getClass())){
                        return failure.apply(th.getCause());
                    }
                    else {
                        return failure.apply(new HandlerException(500, th.getCause().getMessage(), th));
                    }
                }).join();
    }

    public void OnSuccess(Function<R, CompletableFuture<HandlerResult>> completer) {
        this.success = completer;
    }

    public void OnFailure(Function<Throwable, HandlerResult> failed) {
        this.failure = failed;
    }
}
