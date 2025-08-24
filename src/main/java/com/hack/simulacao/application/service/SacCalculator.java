package com.hack.simulacao.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hack.simulacao.domain.h2.Parcela;

@Service
public class SacCalculator {

    public List<Parcela> calcular(BigDecimal valor, int prazo, BigDecimal taxa, String tipo) {
        List<Parcela> parcelas = new ArrayList<>();

        BigDecimal amortizacao = valor.divide(BigDecimal.valueOf(prazo), 2, RoundingMode.HALF_UP);
        BigDecimal saldo = valor;

        for (int i = 1; i <= prazo; i++) {
            BigDecimal juros = saldo.multiply(taxa).setScale(2, RoundingMode.HALF_UP);
            BigDecimal prestacao = amortizacao.add(juros).setScale(2, RoundingMode.HALF_UP);

            Parcela p = new Parcela.Builder()
                .numero(i)
                .valorAmortizacao(amortizacao)
                .valorJuros(juros)
                .valorPrestacao(prestacao)
                .tipo(tipo)
                .build();
            parcelas.add(p);

            saldo = saldo.subtract(amortizacao).setScale(2, RoundingMode.HALF_UP);
        }
        return parcelas;
    }
}
