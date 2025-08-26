package com.hack.simulacao.infra.repository.h2;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hack.simulacao.domain.h2.Simulacao;
import com.hack.simulacao.infra.repository.h2.projection.VolumeProdutoDiaProjection;

public interface SimulacaoRepository extends JpaRepository<Simulacao, Long> {

    @EntityGraph(attributePaths = "parcelas")
    @Query("select s from Simulacao s")
    Page<Simulacao> findAllWithParcelas(Pageable pageable);

    @Query(value = """
            SELECT 
            CAST(s.created_at AS DATE) AS dataReferencia,
            s.codigo_produto AS codigoProduto,
            s.descricao_produto AS descricaoProduto,
            ROUND(AVG(s.taxa_juros), 4) AS taxaMedia,
            ROUND(AVG(pp.valorMedioPrestacaoSAC), 2) AS valorMedioPrestacaoSAC,
            ROUND(AVG(pp.valorMedioPrestacaoPRICE), 2) AS valorMedioPrestacaoPRICE,
            SUM(s.valor_desejado) AS valorTotalDesejado,
            SUM(pp.valorTotalCreditoSAC) AS valorTotalCreditoSAC,
            SUM(pp.valorTotalCreditoPRICE) AS valorTotalCreditoPRICE
            FROM SIMULACAO s
            JOIN (
            SELECT
            p.simulacao_id,
            AVG((CASE WHEN p.tipo = 'SAC' THEN p.valor_prestacao END)) AS valorMedioPrestacaoSAC,
            AVG((CASE WHEN p.tipo = 'PRICE' THEN p.valor_prestacao END)) AS valorMedioPrestacaoPRICE,
            SUM((CASE WHEN p.tipo = 'SAC' THEN p.valor_prestacao END)) AS valorTotalCreditoSAC,
            SUM((CASE WHEN p.tipo = 'PRICE' THEN p.valor_prestacao END)) AS valorTotalCreditoPRICE
            FROM PARCELA p
            GROUP BY p.simulacao_id
            ) pp ON pp.simulacao_id = s.id_simulacao
            WHERE CAST(s.created_at AS DATE) = :data
            GROUP BY CAST(s.created_at AS DATE), s.codigo_produto, s.descricao_produto
            """,
            nativeQuery = true)
            List<VolumeProdutoDiaProjection> volumePorProdutoDia(@Param("data") LocalDate data);
}
