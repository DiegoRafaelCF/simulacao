package com.hack.simulacao.infra.repository.h2;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hack.simulacao.domain.h2.Telemetria;
import com.hack.simulacao.infra.repository.h2.projection.TelemetriaProjection;

public interface TelemetriaRepository extends JpaRepository<Telemetria, Long>  {
    @Query("""
            SELECT 
            CASE
            WHEN t.endpoint = '/api/v1/simulacoes' THEN 'Simulação'
            WHEN t.endpoint = '/api/v1/simulacoes/lista' THEN 'Lista'
            WHEN t.endpoint = '/api/v1/simulacoes/volume-produto-dia' THEN 'Volume'
            ELSE t.endpoint
            END as endpointNormalizado, 
            COUNT(*) as qtdRequisicoes,
            ROUND(AVG(t.tempoMs), 0) as tempoMedio,
            MIN(t.tempoMs) as tempoMinimo,
            MAX(t.tempoMs) as tempoMaximo,
            ROUND(SUM(CASE WHEN t.statusCode = 200 THEN 1 ELSE 0 END) * 1.0 / COUNT(*), 2) as percentualSucesso
            FROM Telemetria t
            WHERE CAST(t.dataEvento AS DATE) = :data
            AND t.endpoint <> '/api/v1/telemetria'
            GROUP BY
            CASE
            WHEN t.endpoint = '/api/v1/simulacoes' THEN 'Simulação'
            WHEN t.endpoint = '/api/v1/simulacoes/lista' THEN 'Lista'
            WHEN t.endpoint = '/api/v1/simulacoes/volume-produto-dia' THEN 'Volume'
            ELSE t.endpoint
            END
            """)
            List<TelemetriaProjection> resumoTelemetria(@Param("data") LocalDate data);
}
