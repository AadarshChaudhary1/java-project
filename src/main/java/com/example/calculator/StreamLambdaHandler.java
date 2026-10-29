package com.example.calculator;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.model.HttpApiV2ProxyRequest;
import com.amazonaws.serverless.proxy.spring.SpringbootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * AWS Lambda handler entrypoint routing Lambda Function URL (HTTP API v2) events
 * directly into the Spring Boot application context and DispatcherServlet.
 */
public class StreamLambdaHandler implements RequestStreamHandler {
    private static final SpringbootLambdaContainerHandler<HttpApiV2ProxyRequest, AwsProxyResponse> handler;

    static {
        try {
            handler = SpringbootLambdaContainerHandler.getHttpApiV2ProxyHandler(CalculatorApplication.class);
        } catch (ContainerInitializationException exception) {
            throw new RuntimeException("Could not initialize Spring Boot Lambda container handler", exception);
        }
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
        handler.proxyStream(inputStream, outputStream, context);
    }
}

