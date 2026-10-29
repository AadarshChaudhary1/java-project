package com.example.calculator;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class CalculatorApiTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void servesCalculatorWebsiteAndItsJavaScript() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(containsString("id=\"bmi-form\"")));

        mockMvc.perform(get("/app.js"))
                .andExpect(status().isOk());
    }

    @Test
    void healthEndpointReportsUp() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void bmiEndpointCalculatesAndValidatesInput() throws Exception {
        mockMvc.perform(post("/api/bmi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"weightKg\":70,\"heightMeters\":1.75}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("Normal weight"));

        mockMvc.perform(post("/api/bmi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"weightKg\":0,\"heightMeters\":1.75}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void mathEndpointUsesJsonAndReturnsClientErrorsForInvalidOperations() throws Exception {
        mockMvc.perform(post("/api/math")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"operation\":\"ADD\",\"firstValue\":2,\"secondValue\":3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(5.0));

        mockMvc.perform(post("/api/math")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"operation\":\"DIVIDE\",\"firstValue\":2,\"secondValue\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void conversionAndGeometryEndpointsCalculateResults() throws Exception {
        mockMvc.perform(post("/api/conversions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"category\":\"LENGTH\",\"conversion\":\"METERS_TO_FEET\",\"value\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(3.28084));

        mockMvc.perform(post("/api/geometry/area")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"shape\":\"CIRCLE\",\"firstDimension\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Math.PI * 4));

        mockMvc.perform(post("/api/geometry/volume")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"shape\":\"CUBOID\",\"firstDimension\":2,\"secondDimension\":3,\"thirdDimension\":4}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(24.0));
    }

    @Test
    void guessingGameCanStartAndAcceptAGuess() throws Exception {
        MvcResult started = mockMvc.perform(post("/api/games/guess/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andReturn();
        String gameId = objectMapper.readTree(started.getResponse().getContentAsString())
                .path("gameId")
                .asText();

        mockMvc.perform(post("/api/games/guess/{gameId}", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"guess\":50}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attempts").value(1))
                .andExpect(jsonPath("$.correct").exists());
    }
}
