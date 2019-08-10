package com.practicaldime.zesty.servlet;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HandlerPromise<R, U> {

    private Function<R, CompletableFuture<U>> success;
    private Function<Throwable, Void> failure;

    public CompletableFuture<U> resolve(CompletableFuture<R> action) {
        return action.thenCompose(r -> success.apply(r))
                .exceptionally(th -> {
                    if(HandlerException.class.isAssignableFrom(th.getCause().getClass())){
                        failure.apply(th.getCause());
                    }
                    else {
                        failure.apply(new HandlerException(500, th.getCause().getMessage(), th));
                    }
                    return null;
                });
    }

    public CompletableFuture<U> complete() {
        return success.apply(null)
                .exceptionally(th -> {
                    if(HandlerException.class.isAssignableFrom(th.getCause().getClass())){
                        failure.apply(th.getCause());
                    }
                    else {
                        failure.apply(new HandlerException(500, th.getCause().getMessage(), th));
                    }
                    return null;
                });
    }

    public void OnSuccess(Function<R, CompletableFuture<U>> completer) {
        this.success = completer;
    }

    public void OnFailure(Function<Throwable, Void> failed) {
        this.failure = failed;
    }
}
