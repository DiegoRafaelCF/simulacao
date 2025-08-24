package com.hack.simulacao.application.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hack.simulacao.api.dto.ListaEndpoints;
import com.hack.simulacao.api.dto.TelemetriaResponse;
import com.hack.simulacao.infra.repository.h2.TelemetriaRepository;

@Service
public class TelemetriaService {

    private final TelemetriaRepository telemetriaRepository;

    public TelemetriaService(TelemetriaRepository telemetriaRepository) {
        this.telemetriaRepository = telemetriaRepository;                      
    }

    public TelemetriaResponse obterTelemetriaPorDia(LocalDate data) {
        List<Object[]> raw = telemetriaRepository.resumoTelemetria(data);

        if(raw.isEmpty()) {
            return new TelemetriaResponse(data, List.of());
        }

        List<ListaEndpoints> list = raw
        .stream()
        .map(o -> new ListaEndpoints(
            (String) o[0],
            ((Number) o[1]).longValue(),
            ((Number) o[2]).doubleValue(),
            ((Number) o[3]).longValue(),
            ((Number) o[4]).longValue(),
            ((Number) o[5]).doubleValue()
        )).toList();

        return new TelemetriaResponse(data, list);
    }

}
