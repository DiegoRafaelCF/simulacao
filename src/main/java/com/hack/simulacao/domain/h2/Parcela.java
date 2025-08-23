package com.hack.simulacao.domain.h2;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PARCELA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Parcela {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "NUMERO")
    private Integer numero;

    @Column(name = "VALOR_AMORTIZACAO", scale = 2)
    private BigDecimal valorAmortizacao;

    @Column(name = "VALOR_JUROS", scale = 2)
    private BigDecimal valorJuros;

    @Column(name = "VALOR_PRESTACAO", scale = 2)
    private BigDecimal valorPrestacao;

    @Column(name = "TIPO")
    private String tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SIMULACAO_ID")
    @JsonBackReference
    private Simulacao simulacao;
}
