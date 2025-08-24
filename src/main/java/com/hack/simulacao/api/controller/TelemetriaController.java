package com.hack.simulacao.api.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hack.simulacao.api.dto.TelemetriaResponse;
import com.hack.simulacao.api.wrapper.ApiResponse;
import com.hack.simulacao.application.service.TelemetriaService;

import jakarta.validation.constraints.PastOrPresent;

@RestController
@RequestMapping("/api/v1/telemetria")
public class TelemetriaController {
    
    private final TelemetriaService telemetriaService;

    public TelemetriaController(TelemetriaService telemetriaService) {
        this.telemetriaService = telemetriaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<TelemetriaResponse>> telemetria(
        @RequestParam("data") @PastOrPresent @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
        ) {
            TelemetriaResponse response = telemetriaService.obterTelemetriaPorDia(data);
        return ResponseEntity.ok(
            new ApiResponse<>(
                response, 
                "Lista de telemetria recuperada", 
                HttpStatus.OK.value()
            )
        );
    }
}
