package com.hack.simulacao.api.dto;

import java.math.BigDecimal;

public record SimulacaoResumoResponse (
    Long idSimulacao,
    BigDecimal valorDesejado,
    Integer prazo,
    BigDecimal totalPrice,
    BigDecimal totalSac
) {}
