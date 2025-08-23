package com.hack.simulacao.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hack.simulacao.api.dto.ListagemSimulacaoResponse;
import com.hack.simulacao.api.dto.ParcelaResponse;
import com.hack.simulacao.api.dto.ResultadoSimulacao;
import com.hack.simulacao.api.dto.SimulacaoRequest;
import com.hack.simulacao.api.dto.SimulacaoResponse;
import com.hack.simulacao.api.dto.SimulacaoResumoResponse;
import com.hack.simulacao.api.dto.ListaEndpoints;
import com.hack.simulacao.api.dto.VolumeProdutoDiaResponse;
import com.hack.simulacao.api.dto.SimulacoesDia;
import com.hack.simulacao.api.dto.TelemetriaResponse;
import com.hack.simulacao.domain.h2.Parcela;
import com.hack.simulacao.domain.h2.Simulacao;
import com.hack.simulacao.infra.messaging.EventHubPublisher;
import com.hack.simulacao.infra.repository.h2.SimulacaoRepository;
import com.hack.simulacao.infra.repository.h2.TelemetriaRepository;
import com.hack.simulacao.infra.repository.sqlserver.ProdutoRepository;

@Service
public class SimulacaoService {
    
    private final ProdutoRepository produtoRepository;
    private final SimulacaoRepository simulacaoRepository;
    private final TelemetriaRepository telemetriaRepository;
    private final EventHubPublisher eventHubPublisher;
    private final SacCalculator sacCalculator;
    private final PriceCalculator priceCalculator;

    public SimulacaoService(ProdutoRepository produtoRepository,
                            SimulacaoRepository simulacaoRepository,
                            TelemetriaRepository telemetriaRepository,
                            EventHubPublisher eventHubPublisher,
                            SacCalculator sacCalculator,
                            PriceCalculator priceCalculator) {
        this.produtoRepository = produtoRepository;  
        this.simulacaoRepository = simulacaoRepository;
        this.telemetriaRepository = telemetriaRepository;
        this.eventHubPublisher = eventHubPublisher; 
        this.sacCalculator = sacCalculator; 
        this.priceCalculator = priceCalculator;                       
    }

    public SimulacaoResponse realizarSimulacao(SimulacaoRequest request) {
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
                        (p.getVrMaximo() == null || request.getValorDesejado().compareTo(p.getVrMaximo()) <= 0) &&
                        request.getPrazo() >= p.getNuMinimoMeses() &&
                        (p.getNuMaximoMeses() == null || request.getPrazo() <= p.getNuMaximoMeses())
                )
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nenhum produto compatível com os critérios informados."));

        List<Parcela> parcelasSac = sacCalculator.calcular(
                request.getValorDesejado(),
                request.getPrazo(),
                produtoSelecionado.getPcTaxaJuros(),
                "SAC");

        List<Parcela> parcelasPrice = priceCalculator.calcular(
                request.getValorDesejado(),
                request.getPrazo(),
                produtoSelecionado.getPcTaxaJuros(),
                "PRICE");

        Simulacao simulacao = new Simulacao();
        simulacao.setCodigoProduto(produtoSelecionado.getCoProduto());
        simulacao.setDescricaoProduto(produtoSelecionado.getNoProduto());
        simulacao.setTaxaJuros(produtoSelecionado.getPcTaxaJuros());
        simulacao.setValorDesejado(request.getValorDesejado());
        simulacao.setPrazo(request.getPrazo());
        
        parcelasSac.forEach(p -> p.setSimulacao(simulacao));
        parcelasPrice.forEach(p -> p.setSimulacao(simulacao));

        simulacao.getParcelas().addAll(parcelasSac);
        simulacao.getParcelas().addAll(parcelasPrice);

        Simulacao saved = simulacaoRepository.save(simulacao);

        eventHubPublisher.publish(saved);

        List<ResultadoSimulacao> resultado = List.of(new ResultadoSimulacao("SAC", parcelasSac.stream().map((Parcela p) -> new ParcelaResponse(p.getNumero(), p.getValorAmortizacao(), p.getValorJuros(), p.getValorPrestacao())).toList()), new ResultadoSimulacao("PRICE", parcelasPrice.stream().map((Parcela p) -> new ParcelaResponse(p.getNumero(), p.getValorAmortizacao(), p.getValorJuros(), p.getValorPrestacao())).toList()));


        return new SimulacaoResponse(
            saved.getIdSimulacao(),
            saved.getCodigoProduto(),
            saved.getDescricaoProduto(),
            saved.getTaxaJuros(),
            resultado
        );
    }

    public ListagemSimulacaoResponse listarSimulacoes(Pageable pageable) {

        Page<SimulacaoResumoResponse> page = simulacaoRepository
        .findAll(pageable)
        .map(simulacao -> {
            BigDecimal totalPrice = simulacao.getParcelas().stream().filter(parcela -> "PRICE".equals(parcela.getTipo()))
            .map(parcela -> parcela.getValorPrestacao())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalSac = simulacao.getParcelas().stream().filter(parcela -> "SAC".equals(parcela.getTipo()))
            .map(parcela -> parcela.getValorPrestacao())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            return new SimulacaoResumoResponse(
                simulacao.getIdSimulacao(),
                simulacao.getValorDesejado(),
                simulacao.getPrazo(),
                totalPrice,
                totalSac
            );
        });

        return new ListagemSimulacaoResponse(
            page.getNumber(),
            page.getTotalElements(),
            page.getSize(),
            page.getContent()
        );
    }

    public VolumeProdutoDiaResponse volumeProdutoDia(LocalDate data) {
        List<Object[]> raw = simulacaoRepository.volumePorProdutoDiaRaw(data);

        List<SimulacoesDia> produtos = raw.stream().map(o -> new SimulacoesDia(
            (Integer) o[1],
            (String) o[2],
            (BigDecimal) o[3],
            (BigDecimal) o[4],
            (BigDecimal) o[5],
            (BigDecimal) o[6],
            (BigDecimal) o[7],
            (BigDecimal) o[8]
        )).toList();

        return new VolumeProdutoDiaResponse(data, produtos);
    }

    public TelemetriaResponse listarTelemetria(LocalDate data) {
        List<Object[]> raw = telemetriaRepository.resumoTelemetria(data);

        if(raw.isEmpty()) {
            return new TelemetriaResponse(data, List.of());
        }

        List<ListaEndpoints> list = raw
        .stream()
        .map(o -> new ListaEndpoints(
            (String) o[0],
            ((Number) o[1]).longValue(),
            ((Number) o[2]).doubleValue(),
            ((Number) o[3]).longValue(),
            ((Number) o[4]).longValue(),
            ((Number) o[5]).doubleValue()
        )).toList();

        return new TelemetriaResponse(data, list);
    }

}
