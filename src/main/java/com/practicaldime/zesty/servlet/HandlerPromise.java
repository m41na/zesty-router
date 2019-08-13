package com.practicaldime.zesty.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HandlerPromise<R> {

    private static final Logger LOG = LoggerFactory.getLogger(HandlerPromise.class);
    private Function<HandlerResult, HandlerResult> success;
    private Function<Throwable, HandlerResult> failure;
    private final HandlerResult result = new HandlerResult();

    public HandlerResult resolve(CompletableFuture<R> action) {
        LOG.info("Start: resolve promise");
        return action.thenCompose(r -> CompletableFuture.completedFuture(result).thenApply(success::apply)
                .exceptionally(th -> {
                    result.updateStatus(Boolean.FALSE);
                    if (HandlerException.class.isAssignableFrom(th.getCause().getClass())) {
                        return failure.apply(th.getCause());
                    } else {
                        return failure.apply(new HandlerException(500, th.getCause().getMessage(), th));
                    }
                })).join();
    }

    public HandlerResult complete() {
        LOG.info("Start: complete promise");
        return CompletableFuture.completedFuture(result).thenApply(success::apply)
                .exceptionally(th -> {
                    result.updateStatus(Boolean.FALSE);
                    if (HandlerException.class.isAssignableFrom(th.getCause().getClass())) {
                        return failure.apply(th.getCause());
                    } else {
                        return failure.apply(new HandlerException(500, th.getCause().getMessage(), th));
                    }
                }).join();
    }

    public void OnSuccess(Function<HandlerResult, HandlerResult> completer) {
        this.success = completer;
    }

    public void OnFailure(Function<Throwable, HandlerResult> failed) {
        this.failure = failed;
    }
}
