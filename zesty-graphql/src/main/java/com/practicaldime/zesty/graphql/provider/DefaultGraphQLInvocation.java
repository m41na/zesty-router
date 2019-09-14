package com.practicaldime.zesty.graphql.provider;

import com.practicaldime.zesty.graphql.api.GraphQLInvocation;
import com.practicaldime.zesty.graphql.api.GraphQLInvocationData;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class DefaultGraphQLInvocation implements GraphQLInvocation {

    @Autowired
    private GraphQL graphQL;

    @Override
    public CompletableFuture<ExecutionResult> invoke(GraphQLInvocationData invocationData) {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(invocationData.getQuery())
                .operationName(invocationData.getOperationName())
                .variables(invocationData.getVariables())
                .build();
        return graphQL.executeAsync(executionInput);
    }
}

