package com.hack.simulacao.infra.repository.h2.projection;

public interface TelemetriaProjection {
    String getEndpointNormalizado();
    Long getQtdRequisicoes();
    Double getTempoMedio();
    Long getTempoMinimo();
    Long getTempoMaximo();
    Double getPercentualSucesso();
}
