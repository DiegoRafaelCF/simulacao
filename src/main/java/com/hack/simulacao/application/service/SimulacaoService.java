package com.hack.simulacao.application.service;

import org.springframework.stereotype.Service;

import com.hack.simulacao.domain.Simulacao;
import com.hack.simulacao.infra.messaging.EventHubPublisher;
import com.hack.simulacao.infra.repository.ProdutoRepository;
import com.hack.simulacao.infra.repository.SimulacaoRepository;

@Service
public class SimulacaoService {
    
    private final ProdutoRepository produtoRepository;
    private final SimulacaoRepository simulacaoRepository;
    private final EventHubPublisher eventHubPublisher;
    private final SacCalculator sacCalculator;
    private final PriceCalculator priceCalculator;

    public SimulacaoService(ProdutoRepository produtoRepository,
                            SimulacaoRepository simulacaoRepository,
                            EventHubPublisher eventHubPublisher,
                            SacCalculator sacCalculator,
                            PriceCalculator priceCalculator) {
        this.produtoRepository = produtoRepository;  
        this.simulacaoRepository = simulacaoRepository; 
        this.eventHubPublisher = eventHubPublisher; 
        this.sacCalculator = sacCalculator; 
        this.priceCalculator = priceCalculator;                       
    }

    public Simulacao realizarSimulacao(Simulacao request) {
        if (request.getValorDesejado() == null || request.getPrazo() == null) {
            throw new IllegalArgumentException("Valor desejado e prazo são obrigatórios.");
        }
        if (request.getValorDesejado().doubleValue() <= 0) {
            throw new IllegalArgumentException("Valor desejado deve ser maior que zero.");
        }
        if (request.getPrazo() < 1) {
            throw new IllegalArgumentException("Prazo deve ser maior ou igual a 1 mês.");
        }

        var produtos = produtoRepository.findAll();
        if (produtos.isEmpty()) {
            throw new IllegalStateException("Nenhum produto cadastrado.");
        }

        var produtoSelecionado = produtos.stream()
                .filter(p ->
                        request.getValorDesejado().compareTo(p.getVrMinimo()) >= 0 &&
                        request.getValorDesejado().compareTo(p.getVrMaximo()) <= 0 &&
                        request.getPrazo() >= p.getNuMinimoMeses() &&
                        request.getPrazo() <= p.getNuMaximoMeses()
                )
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nenhum produto compatível com os critérios informados."));

        var parcelasSac = sacCalculator.calcular(
                request.getValorDesejado(),
                request.getPrazo(),
                produtoSelecionado.getPcTaxaJuros());

        var parcelasPrice = priceCalculator.calcular(
                request.getValorDesejado(),
                request.getPrazo(),
                produtoSelecionado.getPcTaxaJuros());

        Simulacao simulacao = new Simulacao();
        simulacao.setCodigoProduto(produtoSelecionado.getCoProduto());
        simulacao.setDescricaoProduto(produtoSelecionado.getNoProduto());
        simulacao.setTaxaJuros(produtoSelecionado.getPcTaxaJuros());
        simulacao.setValorDesejado(request.getValorDesejado());
        simulacao.setPrazo(request.getPrazo());
        simulacao.setSacParcelas(parcelasSac);
        simulacao.setPriceParcelas(parcelasPrice);

        Simulacao saved = simulacaoRepository.save(simulacao);

        eventHubPublisher.publish(saved);

        return saved;
    }

}
