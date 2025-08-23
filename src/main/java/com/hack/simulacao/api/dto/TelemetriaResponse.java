package com.hack.simulacao.api.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TelemetriaResponse {
    private LocalDate dataReferencia;
    private List<ListaEndpoints> listaEndpoints;
}
