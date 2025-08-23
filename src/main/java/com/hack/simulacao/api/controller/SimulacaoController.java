package com.hack.simulacao.api.controller;

import java.time.LocalDate;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hack.simulacao.api.dto.SimulacaoRequest;
import com.hack.simulacao.api.dto.SimulacaoResponse;
import com.hack.simulacao.application.service.SimulacaoService;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/simulacao")
public class SimulacaoController {
    
    private final SimulacaoService simulacaoService;

    public SimulacaoController(SimulacaoService simulacaoService) {
        this.simulacaoService = simulacaoService;
    }

    @PostMapping
    public ResponseEntity<?> criarSimulacao(@RequestBody SimulacaoRequest request) {
        SimulacaoResponse response = simulacaoService.realizarSimulacao(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/lista")
    public ResponseEntity<?> listarSimulacoes(
        @RequestParam(defaultValue = "0") @PositiveOrZero int page,
        @RequestParam(defaultValue = "10") @Positive int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(simulacaoService.listarSimulacoes(pageable));
    }

    @GetMapping("/produtoPorDia")
    public ResponseEntity<?> volumePorProdutoDia(
        @RequestParam("data") @PastOrPresent @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
        ) {
        return ResponseEntity.ok(simulacaoService.volumeProdutoDia(data));
    }

    @GetMapping("/telemetria")
    public ResponseEntity<?> telemetria(
        @RequestParam("data") @PastOrPresent @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
        ) {
        return ResponseEntity.ok(simulacaoService.listarTelemetria(data));
    }
}
