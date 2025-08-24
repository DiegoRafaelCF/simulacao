package com.hack.simulacao.api.dto;

import java.time.LocalDate;
import java.util.List;

public record VolumeProdutoDiaResponse (
    LocalDate dataReferencia,
    List<SimulacoesDia> simulacoes
) {}
