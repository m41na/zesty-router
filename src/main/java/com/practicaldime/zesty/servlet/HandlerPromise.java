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
        return action.handle((res, th) -> {
            if(th != null) {
                result.updateStatus(Boolean.FALSE);
                if (th.getCause() != null){
                    if(HandlerException.class.isAssignableFrom(th.getCause().getClass())){
                        return failure.apply(result, th.getCause());
                    }
                    else {
                        return failure.apply(result, new HandlerException(500, "promise resolver exception: " + th.getCause().getMessage(), th.getCause()));
                    }
                } else {
                    return failure.apply(result, new HandlerException(500, "promise resolver exception: " + th.getMessage(), th));
                }
            }
            else{
                return success.apply(result);
            }
        }).join();
    }

    public HandlerResult complete() {
        LOG.info("Now will complete promise");
        return success.apply(result);
    }

    public void OnSuccess(Function<HandlerResult, HandlerResult> completer) {
        this.success = completer;
    }

    public void OnFailure(BiFunction<HandlerResult, Throwable, HandlerResult> failed) {
        this.failure = failed;
    }
}
