package com.hack.simulacao.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hack.simulacao.domain.Simulacao;

public interface SimulacaoRepository extends JpaRepository<Simulacao, Long> {
}
