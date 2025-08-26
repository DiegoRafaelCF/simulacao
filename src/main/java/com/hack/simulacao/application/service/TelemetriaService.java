package com.hack.simulacao.application.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hack.simulacao.api.dto.ListaEndpoints;
import com.hack.simulacao.api.dto.TelemetriaResponse;
import com.hack.simulacao.infra.repository.h2.TelemetriaRepository;
import com.hack.simulacao.infra.repository.h2.projection.TelemetriaProjection;

@Service
public class TelemetriaService {

    private final TelemetriaRepository telemetriaRepository;

    public TelemetriaService(TelemetriaRepository telemetriaRepository) {
        this.telemetriaRepository = telemetriaRepository;                      
    }

    public TelemetriaResponse obterTelemetriaPorDia(LocalDate data) {
        List<TelemetriaProjection> raw = telemetriaRepository.resumoTelemetria(data);

        if(raw.isEmpty()) {
            return new TelemetriaResponse(data, List.of());
        }

        List<ListaEndpoints> list = raw
        .stream()
        .map(t -> new ListaEndpoints(
            t.getEndpointNormalizado(),
            t.getQtdRequisicoes(),
            t.getTempoMedio(),
            t.getTempoMinimo(),
            t.getTempoMaximo(),
            t.getPercentualSucesso()
        )).toList();

        return new TelemetriaResponse(data, list);
    }

}
