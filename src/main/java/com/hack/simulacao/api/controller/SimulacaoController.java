package com.hack.simulacao.api.controller;

import java.time.LocalDate;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hack.simulacao.api.dto.ListagemSimulacaoResponse;
import com.hack.simulacao.api.dto.SimulacaoRequest;
import com.hack.simulacao.api.dto.SimulacaoResponse;
import com.hack.simulacao.api.dto.VolumeProdutoDiaResponse;
import com.hack.simulacao.api.wrapper.ApiResponse;
import com.hack.simulacao.application.service.SimulacaoService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/api/v1/simulacoes")
public class SimulacaoController {
    
    private final SimulacaoService simulacaoService;

    public SimulacaoController(SimulacaoService simulacaoService) {
        this.simulacaoService = simulacaoService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SimulacaoResponse>> criarSimulacao(@RequestBody @Valid SimulacaoRequest request) {
        SimulacaoResponse response = simulacaoService.realizarSimulacao(request);
        return ResponseEntity.ok(
            new ApiResponse<>(
                response,
                "Simulacação criada com sucesso",
                HttpStatus.CREATED.value()
            )
        );
    }
    
    @GetMapping("/lista")
    public ResponseEntity<ApiResponse<ListagemSimulacaoResponse>> listarSimulacoes(
        @RequestParam(defaultValue = "0") @PositiveOrZero int page,
        @RequestParam(defaultValue = "10") @Positive int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        ListagemSimulacaoResponse response = simulacaoService.listarSimulacoes(pageable);
        return ResponseEntity.ok(
            new ApiResponse<>(
                response, 
                "Lista de simulações recuperada", 
                HttpStatus.OK.value()
            )
        );
    }

    @GetMapping("/volume-produto-dia")
    public ResponseEntity<ApiResponse<VolumeProdutoDiaResponse>> volumePorProdutoDia(
        @RequestParam("data") @PastOrPresent @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
        ) {
        VolumeProdutoDiaResponse response = simulacaoService.obterVolumeProdutoPorDia(data);
        return ResponseEntity.ok(
            new ApiResponse<>(
                response, 
                "Lista de volume de produto por dia recuperada", 
                HttpStatus.OK.value()
            )
        );
    }
}
