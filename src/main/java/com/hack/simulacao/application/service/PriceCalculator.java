package com.hack.simulacao.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hack.simulacao.domain.Parcela;

@Service
public class PriceCalculator {

    public List<Parcela> calcular(BigDecimal valor, int prazo, BigDecimal taxa) {
        List<Parcela> parcelas = new ArrayList<>();

        BigDecimal i = taxa;
        BigDecimal base = (BigDecimal.ONE.add(i)).pow(prazo);
        BigDecimal coef = (i.multiply(base)).divide(base.subtract(BigDecimal.ONE), 10, RoundingMode.HALF_UP);

        BigDecimal valorParcela = valor.multiply(coef);
        BigDecimal saldo = valor;

        for (int numero = 1; numero <= prazo; numero++) {
            BigDecimal juros = saldo.multiply(i);
            BigDecimal amortizacao = valorParcela.subtract(juros);

            parcelas.add(new Parcela(numero, amortizacao, juros, valorParcela));
            saldo = saldo.subtract(amortizacao);
        }
        return parcelas;
    }
}
