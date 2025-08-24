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

    @Column(name = "VALOR_AMORTIZACAO", precision = 5, scale = 2)
    private BigDecimal valorAmortizacao;

    @Column(name = "VALOR_JUROS", precision = 5, scale = 2)
    private BigDecimal valorJuros;

    @Column(name = "VALOR_PRESTACAO", precision = 5, scale = 2)
    private BigDecimal valorPrestacao;

    @Column(name = "TIPO")
    private String tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SIMULACAO_ID")
    @JsonBackReference
    private Simulacao simulacao;

    public static class Builder {
        private final Parcela parcela = new Parcela();

        public Builder numero(Integer numero) { parcela.numero = numero; return this; }
        public Builder valorAmortizacao(BigDecimal valor) { parcela.valorAmortizacao = valor; return this; }
        public Builder valorJuros(BigDecimal valor) { parcela.valorJuros = valor; return this; }
        public Builder valorPrestacao(BigDecimal valor) { parcela.valorPrestacao = valor; return this; }
        public Builder tipo(String tipo) { parcela.tipo = tipo; return this; }
        public Builder simulacao(Simulacao simulacao) { parcela.simulacao = simulacao; return this; }

        public Parcela build() { return parcela; }
    }
}
