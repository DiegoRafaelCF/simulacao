package com.hack.simulacao.api.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimulacoesDia {
    private Integer codigoProduto;
    private String descricaoProduto;
    private BigDecimal taxaMediaJuro;
    private BigDecimal valorMedioPrestacaoSAC;
    private BigDecimal valorMedioPrestacaoPRICE;
    private BigDecimal valorTotalDesejado;
    private BigDecimal valorTotalCreditoSAC;
    private BigDecimal valorTotalCreditoPRICE;
}
