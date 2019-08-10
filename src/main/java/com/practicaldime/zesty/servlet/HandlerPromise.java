package com.practicaldime.zesty.servlet;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HandlerPromise<R, U> {

    private Function<R, CompletableFuture<U>> success;
    private Function<Throwable, Void> failure;

    public CompletableFuture<U> resolve(CompletableFuture<R> action) {
        return action.thenCompose(r -> success.apply(r))
                .exceptionally(th -> {
                    failure.apply(th);
                    throw new RuntimeException(th);
                });
    }

    public CompletableFuture<U> complete() {
        return success.apply(null)
                .exceptionally(th -> {
                    failure.apply(th);
                    throw new RuntimeException(th);
                });
    }

    public void OnSuccess(Function<R, CompletableFuture<U>> completer) {
        this.success = completer;
    }

    public void OnFailure(Function<Throwable, Void> failed) {
        this.failure = failed;
    }
}
