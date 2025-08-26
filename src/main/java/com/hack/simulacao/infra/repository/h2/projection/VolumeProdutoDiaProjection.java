package com.hack.simulacao.infra.repository.h2.projection;

import java.math.BigDecimal;

public interface VolumeProdutoDiaProjection {
    Integer getCodigoProduto();
    String getDescricaoProduto();
    BigDecimal getTaxaMedia();
    BigDecimal getValorMedioPrestacaoSAC();
    BigDecimal getValorMedioPrestacaoPRICE();
    BigDecimal getValorTotalDesejado();
    BigDecimal getValorTotalCreditoSAC();
    BigDecimal getValorTotalCreditoPRICE();
}
