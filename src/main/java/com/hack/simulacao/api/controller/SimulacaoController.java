package com.hack.simulacao.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hack.simulacao.application.service.SimulacaoService;
import com.hack.simulacao.domain.Simulacao;

@RestController
@RequestMapping("/simulacao")
public class SimulacaoController {
    
    private final SimulacaoService simulacaoService;

    public SimulacaoController(SimulacaoService simulacaoService) {
        this.simulacaoService = simulacaoService;
    }

    @PostMapping
    public ResponseEntity<?> criarSimulacao(@RequestBody Simulacao request) {
        var response = simulacaoService.realizarSimulacao(request);
        return ResponseEntity.ok(response);
    }
}
