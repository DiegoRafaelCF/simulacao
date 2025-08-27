package com.hack.simulacao.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TelemetriaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void telemetria_DeveRetornar200() throws Exception {
        String data = java.time.LocalDate.now().toString();
        mockMvc.perform(get("/api/v1/telemetria")
                .param("data", data))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void telemetria_DiaFuturo_DeveRetornar400() throws Exception {
        String data = java.time.LocalDate.now().plusDays(1).toString();
        mockMvc.perform(get("/api/v1/telemetria")
                .param("data", data))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void telemetria_SemData_DeveRetornar400() throws Exception {
        mockMvc.perform(get("/api/v1/telemetria"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
