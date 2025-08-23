package com.hack.simulacao.api.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SimulacaoRequest {
    private BigDecimal valorDesejado;
    private Integer prazo;
}
