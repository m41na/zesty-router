package com.practicaldime.zesty.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HandlerPromise<R, U> {

    private static final Logger LOG = LoggerFactory.getLogger(HandlerPromise.class);
    private Function<R, CompletableFuture<HandlerResult>> success;
    private Function<Throwable, HandlerResult> failure;

    public CompletableFuture<HandlerResult> resolve(CompletableFuture<R> action) {
        LOG.info("Start: resolve promise");
        return action.thenCompose(r -> success.apply(r))
                .exceptionally(th -> {
                    if(HandlerException.class.isAssignableFrom(th.getCause().getClass())){
                        failure.apply(th.getCause());
                    }
                    else {
                        failure.apply(new HandlerException(500, th.getCause().getMessage(), th));
                    }
                    return HandlerResult.build(Boolean.FALSE);
                });
    }

    public CompletableFuture<HandlerResult> complete() {
        LOG.info("Start: complete promise");
        return success.apply(null)
                .exceptionally(th -> {
                    if(HandlerException.class.isAssignableFrom(th.getCause().getClass())){
                        failure.apply(th.getCause());
                    }
                    else {
                        failure.apply(new HandlerException(500, th.getCause().getMessage(), th));
                    }
                    return HandlerResult.build(Boolean.FALSE);
                });
    }

    public void OnSuccess(Function<R, CompletableFuture<HandlerResult>> completer) {
        this.success = completer;
    }

    public void OnFailure(Function<Throwable, HandlerResult> failed) {
        this.failure = failed;
    }
}
