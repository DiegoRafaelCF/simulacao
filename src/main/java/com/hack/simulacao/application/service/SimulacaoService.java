package com.hack.simulacao.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hack.simulacao.api.dto.ListagemSimulacaoResponse;
import com.hack.simulacao.api.dto.ResultadoSimulacao;
import com.hack.simulacao.api.dto.SimulacaoRequest;
import com.hack.simulacao.api.dto.SimulacaoResponse;
import com.hack.simulacao.api.dto.SimulacaoResumoResponse;
import com.hack.simulacao.api.dto.VolumeProdutoDiaResponse;
import com.hack.simulacao.api.error.exceptions.ProdutoNaoEncontradoException;
import com.hack.simulacao.api.mapper.ParcelaMapper;
import com.hack.simulacao.api.dto.SimulacoesDia;
import com.hack.simulacao.domain.h2.Parcela;
import com.hack.simulacao.domain.h2.Simulacao;
import com.hack.simulacao.domain.sqlserver.Produto;
import com.hack.simulacao.infra.messaging.EventHubPublisher;
import com.hack.simulacao.infra.repository.h2.SimulacaoRepository;
import com.hack.simulacao.infra.repository.h2.projection.VolumeProdutoDiaProjection;
import com.hack.simulacao.infra.repository.sqlserver.ProdutoRepository;

@Service
public class SimulacaoService {
    
    private final ProdutoRepository produtoRepository;
    private final SimulacaoRepository simulacaoRepository;
    private final EventHubPublisher eventHubPublisher;
    private final SacCalculator sacCalculator;
    private final PriceCalculator priceCalculator;
    private final ParcelaMapper parcelaMapper;

    public SimulacaoService(ProdutoRepository produtoRepository,
                            SimulacaoRepository simulacaoRepository,
                            EventHubPublisher eventHubPublisher,
                            SacCalculator sacCalculator,
                            PriceCalculator priceCalculator,
                            ParcelaMapper parcelaMapper
                            ) {
        this.produtoRepository = produtoRepository;  
        this.simulacaoRepository = simulacaoRepository;
        this.eventHubPublisher = eventHubPublisher; 
        this.sacCalculator = sacCalculator; 
        this.priceCalculator = priceCalculator;  
        this.parcelaMapper = parcelaMapper;                     
    }

    @Transactional
    public SimulacaoResponse realizarSimulacao(SimulacaoRequest request) {

        Produto produtoSelecionado = selecionarProduto(request);
        
        List<Parcela> parcelasSac = sacCalculator.calcular(
                request.valorDesejado(),
                request.prazo(),
                produtoSelecionado.getPcTaxaJuros(),
                "SAC");

        List<Parcela> parcelasPrice = priceCalculator.calcular(
                request.valorDesejado(),
                request.prazo(),
                produtoSelecionado.getPcTaxaJuros(),
                "PRICE");

        Simulacao simulacao = montarSimulacao(request, produtoSelecionado, parcelasSac, parcelasPrice);

        Simulacao saved = simulacaoRepository.save(simulacao);

        eventHubPublisher.publish(saved);

        return montarResponse(saved, parcelasSac, parcelasPrice);
    }

    @Transactional(readOnly = true)
    public ListagemSimulacaoResponse listarSimulacoes(Pageable pageable) {

        Page<Simulacao> page = simulacaoRepository.findAllWithParcelas(pageable);

        Page<SimulacaoResumoResponse> mapped = page.map(simulacao ->
            new SimulacaoResumoResponse(
                simulacao.getIdSimulacao(),
                simulacao.getValorDesejado(),
                simulacao.getPrazo(),
                simulacao.totalPrice(),
                simulacao.totalSac()
            )
        );

        return new ListagemSimulacaoResponse(
            mapped.getNumber(),
            mapped.getTotalElements(),
            mapped.getSize(),
            mapped.getContent()
        );
    }

    @Transactional(readOnly = true)
    public VolumeProdutoDiaResponse obterVolumeProdutoPorDia(LocalDate data) {
        List<VolumeProdutoDiaProjection> raw = simulacaoRepository.volumePorProdutoDia(data);

        List<SimulacoesDia> produtos = raw.stream().map(v -> 
            new SimulacoesDia(
                v.getCodigoProduto(),
                v.getDescricaoProduto(),
                v.getTaxaMedia(),
                v.getValorMedioPrestacaoSAC(),
                v.getValorMedioPrestacaoPRICE(),
                v.getValorTotalDesejado(),
                v.getValorTotalCreditoSAC(),
                v.getValorTotalCreditoPRICE()
        )).toList();

        return new VolumeProdutoDiaResponse(data, produtos);
    }

    private Produto selecionarProduto(SimulacaoRequest request) {
        List<Produto> produtos = produtoRepository.findByCriterios(request.valorDesejado(), request.prazo());

        Produto produtoSelecionado = produtos.stream()
                .findFirst()
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Nenhum produto compatível com os critérios informados."));

        return produtoSelecionado;
    }

    private Simulacao montarSimulacao(
        SimulacaoRequest request, 
        Produto produtoSelecionado, 
        List<Parcela> parcelasSac, 
        List<Parcela> parcelasPrice
    ) {
        Simulacao simulacao = new Simulacao.Builder()
        .codigoProduto(produtoSelecionado.getCoProduto())
        .descricaoProduto(produtoSelecionado.getNoProduto())
        .taxaJuros(produtoSelecionado.getPcTaxaJuros())
        .valorDesejado(request.valorDesejado())
        .prazo(request.prazo())
        .parcelas(Stream.concat(parcelasSac.stream(), parcelasPrice.stream()).toList())
        .build();

        return simulacao;
    }

    private SimulacaoResponse montarResponse(
        Simulacao saved, 
        List<Parcela> parcelasSac, 
        List<Parcela> parcelasPrice
    ) {

        List<ResultadoSimulacao> resultado = List.of(
            new ResultadoSimulacao("SAC", parcelaMapper.toResponse(parcelasSac)), 
            new ResultadoSimulacao("PRICE", parcelaMapper.toResponse(parcelasPrice)));

        return new SimulacaoResponse(
            saved.getIdSimulacao(),
            saved.getCodigoProduto(),
            saved.getDescricaoProduto(),
            saved.getTaxaJuros(),
            resultado
        );
    }

}
