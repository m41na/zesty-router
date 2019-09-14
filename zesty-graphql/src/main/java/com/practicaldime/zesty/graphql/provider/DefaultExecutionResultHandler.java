package com.practicaldime.zesty.graphql.provider;

import com.practicaldime.zesty.graphql.api.ExecutionResultHandler;
import graphql.ExecutionResult;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class DefaultExecutionResultHandler implements ExecutionResultHandler {

    @Override
    public CompletableFuture<Map<String, Object>> handleExecutionResult(CompletableFuture<ExecutionResult> executionResultCF) {
        return executionResultCF.thenApply(ExecutionResult::toSpecification);
    }
}

