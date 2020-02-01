package com.practicaldime.zesty.graphql.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaldime.zesty.graphql.api.ExecutionResultHandler;
import com.practicaldime.zesty.graphql.api.GraphQLInvocation;
import com.practicaldime.zesty.graphql.api.GraphQLInvocationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class GraphQLHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GraphQLHandler.class);

    @Autowired
    private GraphQLInvocation graphQLInvocation;

    @Autowired
    private ExecutionResultHandler executionResultHandler;

    @Autowired
    private ObjectMapper objectMapper;

    public CompletableFuture<byte[]> handle(GraphQLInvocationData data) {
        return executionResultHandler.handleExecutionResult(graphQLInvocation.invoke(data))
                .handle((result, err) -> {
                    if (result == null) {
                        LOG.error("Error processing json content", err);
                    }
                    return result;
                })
                .thenApply(map -> {
                    try {
                        return objectMapper.writeValueAsBytes(map);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public CompletableFuture<byte[]> handle(String query, String operationName, String variablesJson) {
        return handle(new GraphQLInvocationData(query, operationName, convertVariablesJson(variablesJson)));
    }

    private Map<String, Object> convertVariablesJson(String jsonMap) {
        if (jsonMap == null)
            return Collections.emptyMap();
        try {
            return objectMapper.readValue(jsonMap, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Could not convert variables GET parameter: expected a JSON map", e);
        }
    }
}

