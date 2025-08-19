package com.hack.simulacao.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "PRODUTO")
public class Produto {

    @Id
    @Column(name = "CO_PRODUTO")
    private Integer coProduto;

    @Column(name = "NO_PRODUTO")
    private String noProduto;

    @Column(name = "PC_TAXA_JUROS")
    private BigDecimal pcTaxaJuros;

    @Column(name = "NU_MINIMO_MESES")
    private Integer nuMinimoMeses;

    @Column(name = "NU_MAXIMO_MESES")
    private Integer nuMaximoMeses;

    @Column(name = "VR_MINIMO")
    private BigDecimal vrMinimo;

    @Column(name = "VR_MAXIMO")
    private BigDecimal vrMaximo;
}
