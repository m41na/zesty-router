package com.practicaldime.router.core.servlet;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;

public class HandlerPromiseTest {

    private HandlerPromise promise;

    @Before
    public void setUp(){
        promise = new HandlerPromise();
        promise.OnFailure((th, res) -> th);
        promise.OnSuccess(res -> res);
    }

    @Test
    public void resolve() {
        HandlerResult result = promise.resolve(CompletableFuture.completedFuture(true));
        assertTrue(result.isSuccess());

        result = promise.resolve(CompletableFuture.failedFuture(new RuntimeException("major failure")));
        assertFalse(result.isSuccess());
    }

    @Test
    public void complete() {
        HandlerResult result = promise.complete();
        assertTrue(result.isSuccess());
    }
}