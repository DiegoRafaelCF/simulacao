package com.hack.simulacao.api.dto;

import java.math.BigDecimal;

public record ParcelaResponse (

    Integer numero,
    BigDecimal valorAmortizacao,
    BigDecimal valorJuros,
    BigDecimal valorPrestacao
) {}
