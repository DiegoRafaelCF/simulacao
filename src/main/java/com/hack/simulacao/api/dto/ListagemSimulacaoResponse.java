package com.hack.simulacao.api.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ListagemSimulacaoResponse {
    private int pagina;
    private long qtdRegistros;
    private int qtdRegistrosPagina;
    private List<SimulacaoResumoResponse> registros;
}
