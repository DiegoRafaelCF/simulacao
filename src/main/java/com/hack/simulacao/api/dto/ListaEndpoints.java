package com.hack.simulacao.api.dto;

public record ListaEndpoints (
    String nomeApi,
    Long qtdRequisicoes,
    Double tempoMedio,
    Long tempoMinimo,
    Long tempoMaximo,
    Double percentualSucesso
) {}
