package com.practicaldime.zesty.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public class HandlerPromise<R> {

    private static final Logger LOG = LoggerFactory.getLogger(HandlerPromise.class);
    private Function<HandlerResult, HandlerResult> success;
    private BiFunction<HandlerResult, Throwable, HandlerResult> failure;
    private final HandlerResult result = new HandlerResult();

    public HandlerResult resolve(CompletableFuture<R> action) {
        LOG.info("Now will resolve promise");
        return action.thenCompose(r -> CompletableFuture.completedFuture(result).thenApply(success::apply)
                .handle((res, th) -> {
                    if(th != null) {
                        if (HandlerException.class.isAssignableFrom(th.getCause().getClass())) {
                            return failure.apply(res, th.getCause());
                        } else {
                            return failure.apply(res, new HandlerException(500, th.getCause().getMessage(), th));
                        }
                    }
                    res.updateStatus(Boolean.FALSE);
                    return res;
                })).join();
    }

    public HandlerResult complete() {
        LOG.info("Now will complete promise");
        return CompletableFuture.completedFuture(result).thenApply(success::apply)
                .handle((res, th) -> {
                    if(th != null) {
                        if (HandlerException.class.isAssignableFrom(th.getCause().getClass())) {
                            return failure.apply(res, th.getCause());
                        } else {
                            return failure.apply(res, new HandlerException(500, th.getCause().getMessage(), th));
                        }
                    }
                    res.updateStatus(Boolean.FALSE);
                    return res;
                }).join();
    }

    public void OnSuccess(Function<HandlerResult, HandlerResult> completer) {
        this.success = completer;
    }

    public void OnFailure(BiFunction<HandlerResult, Throwable, HandlerResult> failed) {
        this.failure = failed;
    }
}
