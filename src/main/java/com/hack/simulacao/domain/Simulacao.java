package com.hack.simulacao.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "SIMULACAO")
public class Simulacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer codigoProduto;
    private String descricaoProduto;
    private BigDecimal taxaJuros;

    private BigDecimal valorDesejado;
    private Integer prazo;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "PARCELA_SAC", joinColumns = @JoinColumn(name = "SIMULACAO_ID"))
    private List<Parcela> sacParcelas;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "PARCELA_PRICE", joinColumns = @JoinColumn(name = "SIMULACAO_ID"))
    private List<Parcela> priceParcelas;
}
