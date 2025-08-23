package com.hack.simulacao.api.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimulacaoResponse {
    private Long idSimulacao;
    private Integer codigoProduto;
    private String descricaoProduto;
    private BigDecimal taxaJuros;
    private List<ResultadoSimulacao> resultadoSimulacao;
}
