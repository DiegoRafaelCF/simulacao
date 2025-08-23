package com.hack.simulacao.api.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimulacaoResumoResponse {
    private Long idSimulacao;
    private BigDecimal valorDesejado;
    private Integer prazo;
    private BigDecimal valorTotalParcelasPRICE;
    private BigDecimal valorTotalParcelasSAC;
}
