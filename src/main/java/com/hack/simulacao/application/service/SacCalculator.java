package com.hack.simulacao.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hack.simulacao.domain.Parcela;

@Service
public class SacCalculator {

    public List<Parcela> calcular(BigDecimal valor, int prazo, BigDecimal taxa) {
        List<Parcela> parcelas = new ArrayList<>();

        BigDecimal amortizacao = valor.divide(BigDecimal.valueOf(prazo), 2, RoundingMode.HALF_UP);
        BigDecimal saldo = valor;

        for (int i = 1; i <= prazo; i++) {
            BigDecimal juros = saldo.multiply(taxa);
            BigDecimal prestacao = amortizacao.add(juros);

            parcelas.add(new Parcela(i, amortizacao, juros, prestacao));
            saldo = saldo.subtract(amortizacao);
        }
        return parcelas;
    }
}
