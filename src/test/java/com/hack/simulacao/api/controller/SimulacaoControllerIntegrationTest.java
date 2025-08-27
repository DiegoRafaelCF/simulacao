package com.hack.simulacao.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hack.simulacao.api.dto.SimulacaoRequest;
import com.hack.simulacao.domain.h2.Simulacao;
import com.hack.simulacao.infra.messaging.EventHubPublisher;
import com.hack.simulacao.infra.repository.h2.SimulacaoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SimulacaoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SimulacaoRepository simulacaoRepository;

    @MockBean
    private EventHubPublisher eventHubPublisher;

    @BeforeEach
    void setup() {
        simulacaoRepository.deleteAll();

        Mockito.doNothing().when(eventHubPublisher).publish(Mockito.any());
    }


    @Test
    void criarSimulacao_ComDadosValidos_DeveRetornar201() throws Exception {
    var request = new SimulacaoRequest(
        new BigDecimal("500.00"),
        12
    );
    mockMvc.perform(post("/api/v1/simulacoes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(201))
        .andExpect(jsonPath("$.data.idSimulacao").exists())
        .andExpect(jsonPath("$.data.taxaJuros").value(0.0179))
        .andExpect(jsonPath("$.data.resultadoSimulacao[0].parcelas").isArray())
        .andExpect(jsonPath("$.data.resultadoSimulacao[0].parcelas[0].valorPrestacao").exists());

    var simulacoes = simulacaoRepository.findAll();
    assertEquals(1, simulacoes.size());
    
    Simulacao s = simulacoes.get(0);
    assertEquals(500, s.getValorDesejado().intValue());
    assertEquals(12, s.getPrazo().intValue());
    assertFalse(s.getParcelas().isEmpty());
    }

    @Test
    void criarSimulacao_ComDadosValidos_DeveRetornarProduto4() throws Exception {
    var request = new SimulacaoRequest(
        new BigDecimal("10000000.00"),
        100
    );
    mockMvc.perform(post("/api/v1/simulacoes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(201))
        .andExpect(jsonPath("$.data.idSimulacao").exists())
        .andExpect(jsonPath("$.data.codigoProduto").value(4))
        .andExpect(jsonPath("$.data.taxaJuros").value(0.0151))
        .andExpect(jsonPath("$.data.resultadoSimulacao[0].parcelas").isArray())
        .andExpect(jsonPath("$.data.resultadoSimulacao[0].parcelas[0].valorPrestacao").exists());

    var simulacoes = simulacaoRepository.findAll();
    assertEquals(1, simulacoes.size());
    
    Simulacao s = simulacoes.get(0);
    assertEquals(10000000, s.getValorDesejado().intValue());
    assertEquals(100, s.getPrazo().intValue());
    assertFalse(s.getParcelas().isEmpty());
    }

    @Test
    void criarSimulacao_ValorAbaixoMinimo_DeveRetornar400() throws Exception {
    var request = new SimulacaoRequest(
        new BigDecimal("100.00"),
        12
    );
    mockMvc.perform(post("/api/v1/simulacoes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(400));

    var simulacoes = simulacaoRepository.findAll();
    assertEquals(0, simulacoes.size());
    }

    @Test
    void criarSimulacao_PrazoInvalido_DeveRetornar400() throws Exception {
    var request = new SimulacaoRequest(
        new BigDecimal("500.00"),
        0
    );
    mockMvc.perform(post("/api/v1/simulacoes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(400));

    var simulacoes = simulacaoRepository.findAll();
    assertEquals(0, simulacoes.size());
    }

    @Test
    void criarSimulacao_SemProdutoCompativel_DeveRetornar404() throws Exception {
    var request = new SimulacaoRequest(
        new BigDecimal("10000.00"),
        50
    );
    mockMvc.perform(post("/api/v1/simulacoes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(404));

    var simulacoes = simulacaoRepository.findAll();
    assertEquals(0, simulacoes.size());
    }

    @Test
    void listarSimulacoes_DeveRetornar200() throws Exception {
        mockMvc.perform(get("/api/v1/simulacoes/lista")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void listarSimulacoes_PaginaNegativa_DeveRetornar400() throws Exception {
        mockMvc.perform(get("/api/v1/simulacoes/lista")
                .param("page", "-1")
                .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void listarSimulacoes_SizeNegativo_DeveRetornar400() throws Exception {
        mockMvc.perform(get("/api/v1/simulacoes/lista")
                .param("page", "0")
                .param("size", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void volumePorProdutoDia_DeveRetornar200() throws Exception {
        String data = java.time.LocalDate.now().toString();
        mockMvc.perform(get("/api/v1/simulacoes/volume-produto-dia")
                .param("data", data))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void volumePorProdutoDia_DiaFuturo_DeveRetornar400() throws Exception {
        String data = java.time.LocalDate.now().plusDays(1).toString();
        mockMvc.perform(get("/api/v1/simulacoes/volume-produto-dia")
                .param("data", data))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void volumePorProdutoDia_SemData_DeveRetornar400() throws Exception {
        mockMvc.perform(get("/api/v1/simulacoes/volume-produto-dia"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
