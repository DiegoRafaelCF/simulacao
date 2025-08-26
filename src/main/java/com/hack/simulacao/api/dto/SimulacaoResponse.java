package com.hack.simulacao.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record SimulacaoResponse (
    Long idSimulacao,
    Integer codigoProduto,
    String descricaoProduto,
    BigDecimal taxaJuros,
    List<ResultadoSimulacao> resultadoSimulacao
) {}
