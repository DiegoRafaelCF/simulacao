package com.hack.simulacao.api.dto;

import java.time.LocalDate;
import java.util.List;

public record TelemetriaResponse (
    LocalDate dataReferencia,
    List<ListaEndpoints> listaEndpoints
) {}
