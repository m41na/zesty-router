package com.practicaldime.zesty.graphql.api;

import graphql.ExecutionResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ExecutionResultHandler {

    CompletableFuture<Map<String, Object>> handleExecutionResult(CompletableFuture<ExecutionResult> executionResultCF);
}
