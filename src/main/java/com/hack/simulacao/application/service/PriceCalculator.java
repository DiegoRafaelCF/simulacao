package com.hack.simulacao.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hack.simulacao.domain.h2.Parcela;

@Service
public class PriceCalculator {

    public List<Parcela> calcular(BigDecimal valor, int prazo, BigDecimal taxa, String tipo) {
        List<Parcela> parcelas = new ArrayList<>();

        BigDecimal i = taxa;
        BigDecimal base = (BigDecimal.ONE.add(i)).pow(prazo);

        BigDecimal valorParcela = valor
        .multiply(i)
        .multiply(base)
        .divide(base.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
        
        BigDecimal saldo = valor;

        for (int numero = 1; numero <= prazo; numero++) {
            BigDecimal juros = saldo.multiply(i).setScale(2, RoundingMode.HALF_UP);
            BigDecimal amortizacao = valorParcela.subtract(juros).setScale(2, RoundingMode.HALF_UP);

            Parcela p = new Parcela.Builder()
                .numero(numero)
                .valorAmortizacao(amortizacao)
                .valorJuros(juros)
                .valorPrestacao(valorParcela)
                .tipo(tipo)
                .build();
            parcelas.add(p);
            
            saldo = saldo.subtract(amortizacao).setScale(2, RoundingMode.HALF_UP);
        }
        return parcelas;
    }
}
