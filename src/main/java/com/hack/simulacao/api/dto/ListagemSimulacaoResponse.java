package com.hack.simulacao.api.dto;

import java.util.List;

public record ListagemSimulacaoResponse (
    int pagina,
    long qtdRegistros,
    int qtdRegistrosPagina,
    List<SimulacaoResumoResponse> registros
) {}
