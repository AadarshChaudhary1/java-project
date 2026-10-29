package com.example.calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class StreamLambdaHandlerTest {
    private static StreamLambdaHandler lambdaHandler;
    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {
        lambdaHandler = new StreamLambdaHandler();
        objectMapper = new ObjectMapper();
    }

    private JsonNode invoke(String method, String path, String rawBody) throws IOException {
        String bodyJsonValue = rawBody == null ? "null" : objectMapper.writeValueAsString(rawBody);
        String event = """
                {
                  "version": "2.0",
                  "routeKey": "$default",
                  "rawPath": "%s",
                  "rawQueryString": "",
                  "headers": {
                    "accept": "*/*",
                    "content-type": "application/json"
                  },
                  "requestContext": {
                    "http": {
                      "method": "%s",
                      "path": "%s",
                      "protocol": "HTTP/1.1",
                      "sourceIp": "127.0.0.1",
                      "userAgent": "TestClient/1.0"
                    }
                  },
                  "body": %s,
                  "isBase64Encoded": false
                }
                """.formatted(path, method, path, bodyJsonValue);

        ByteArrayInputStream in = new ByteArrayInputStream(event.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        lambdaHandler.handleRequest(in, out, Mockito.mock(Context.class));

        return objectMapper.readTree(out.toByteArray());
    }

    @Test
    void servesHomepageAndStaticAssets() throws Exception {
        JsonNode rootResponse = invoke("GET", "/", null);
        assertEquals(200, rootResponse.path("statusCode").asInt());
        assertTrue(rootResponse.path("body").asText().contains("Everyday Calculators"));

        JsonNode indexResponse = invoke("GET", "/index.html", null);
        assertEquals(200, indexResponse.path("statusCode").asInt());
        assertTrue(indexResponse.path("body").asText().contains("id=\"bmi-form\""));

        JsonNode cssResponse = invoke("GET", "/styles.css", null);
        assertEquals(200, cssResponse.path("statusCode").asInt());
        assertTrue(cssResponse.path("body").asText().contains("--ink"));

        JsonNode jsResponse = invoke("GET", "/app.js", null);
        assertEquals(200, jsResponse.path("statusCode").asInt());
        assertTrue(jsResponse.path("body").asText().contains("postJson"));
    }

    @Test
    void healthEndpointReturnsUp() throws Exception {
        JsonNode response = invoke("GET", "/api/health", null);
        assertEquals(200, response.path("statusCode").asInt());

        JsonNode body = objectMapper.readTree(response.path("body").asText());
        assertEquals("UP", body.path("status").asText());
    }

    @Test
    void mathEndpointComputesAndValidatesErrors() throws Exception {
        JsonNode success = invoke("POST", "/api/math", "{\"operation\":\"ADD\",\"firstValue\":2,\"secondValue\":3}");
        assertEquals(200, success.path("statusCode").asInt());
        JsonNode successBody = objectMapper.readTree(success.path("body").asText());
        assertEquals(5.0, successBody.path("result").asDouble());

        JsonNode divZero = invoke("POST", "/api/math", "{\"operation\":\"DIVIDE\",\"firstValue\":2,\"secondValue\":0}");
        assertEquals(400, divZero.path("statusCode").asInt());
        JsonNode errBody = objectMapper.readTree(divZero.path("body").asText());
        assertTrue(errBody.path("error").asText().contains("Division by zero"));
    }

    @Test
    void bmiEndpointComputesAndRejectsInvalidValues() throws Exception {
        JsonNode success = invoke("POST", "/api/bmi", "{\"weightKg\":70,\"heightMeters\":1.75}");
        assertEquals(200, success.path("statusCode").asInt());
        JsonNode body = objectMapper.readTree(success.path("body").asText());
        assertEquals("Normal weight", body.path("category").asText());

        JsonNode invalid = invoke("POST", "/api/bmi", "{\"weightKg\":-5,\"heightMeters\":1.75}");
        assertEquals(400, invalid.path("statusCode").asInt());
    }

    @Test
    void conversionAndGeometryEndpointsSucceed() throws Exception {
        JsonNode conversion = invoke("POST", "/api/conversions",
                "{\"category\":\"LENGTH\",\"conversion\":\"METERS_TO_FEET\",\"value\":1}");
        assertEquals(200, conversion.path("statusCode").asInt());
        JsonNode convBody = objectMapper.readTree(conversion.path("body").asText());
        assertEquals(3.28084, convBody.path("result").asDouble());

        JsonNode area = invoke("POST", "/api/geometry/area", "{\"shape\":\"CIRCLE\",\"firstDimension\":2}");
        assertEquals(200, area.path("statusCode").asInt());

        JsonNode volume = invoke("POST", "/api/geometry/volume",
                "{\"shape\":\"CUBOID\",\"firstDimension\":2,\"secondDimension\":3,\"thirdDimension\":4}");
        assertEquals(200, volume.path("statusCode").asInt());
        JsonNode volBody = objectMapper.readTree(volume.path("body").asText());
        assertEquals(24.0, volBody.path("result").asDouble());
    }

    @Test
    void guessingGameStartsAndAcceptsGuesses() throws Exception {
        JsonNode start = invoke("POST", "/api/games/guess/start", "{}");
        assertEquals(200, start.path("statusCode").asInt());
        JsonNode startBody = objectMapper.readTree(start.path("body").asText());
        String gameId = startBody.path("gameId").asText();
        assertNotNull(gameId);
        assertFalse(gameId.isBlank());

        JsonNode guess = invoke("POST", "/api/games/guess/" + gameId, "{\"guess\":50}");
        assertEquals(200, guess.path("statusCode").asInt());
        JsonNode guessBody = objectMapper.readTree(guess.path("body").asText());
        assertEquals(1, guessBody.path("attempts").asInt());
    }

    @Test
    void unknownPathReturnsNotFound() throws Exception {
        JsonNode notFound = invoke("GET", "/non-existent-endpoint", null);
        assertEquals(404, notFound.path("statusCode").asInt());
    }
}

