package com.hack.simulacao.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ListaEndpoints {
    private String nomeApi;
    private Long qtdRequisicoes;
    private Double tempoMedio;
    private Long tempoMinimo;
    private Long tempoMaximo;
    private Double percentualSucesso;
}
