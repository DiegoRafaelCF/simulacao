package com.hack.simulacao.domain.h2;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Data
@Entity
@Table(name = "SIMULACAO")
public class Simulacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SIMULACAO")
    private Long idSimulacao;

    @Column(name = "CODIGO_PRODUTO")
    private Integer codigoProduto;

    @Column(name = "DESCRICAO_PRODUTO")
    private String descricaoProduto;

    @Column(name = "TAXA_JUROS", precision = 10, scale = 8)
    private BigDecimal taxaJuros;

    @Column(name = "VALOR_DESEJADO")
    private BigDecimal valorDesejado;
    
    @Column(name = "PRAZO")
    private Integer prazo;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "simulacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Parcela> parcelas = new ArrayList<>();

    public BigDecimal totalPrestacoesPorTipo(String tipo) {
        return parcelas.stream()
                .filter(p -> tipo.equals(p.getTipo()))
                .map(Parcela::getValorPrestacao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalSac()   { return totalPrestacoesPorTipo("SAC"); }
    public BigDecimal totalPrice() { return totalPrestacoesPorTipo("PRICE"); }
}
