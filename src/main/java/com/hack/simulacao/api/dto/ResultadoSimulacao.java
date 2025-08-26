package com.hack.simulacao.api.dto;

import java.util.List;

public record ResultadoSimulacao (
    String tipo,
    List<ParcelaResponse> parcelas
) {}
