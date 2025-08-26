package com.hack.simulacao.api.dto;

import java.math.BigDecimal;

public record SimulacoesDia (
    Integer codigoProduto,
    String descricaoProduto,
    BigDecimal taxaMedia,
    BigDecimal valorMedioPrestacaoSAC,
    BigDecimal valorMedioPrestacaoPRICE,
    BigDecimal valorTotalDesejado,
    BigDecimal valorTotalCreditoSAC,
    BigDecimal valorTotalCreditoPRICE
) {}
