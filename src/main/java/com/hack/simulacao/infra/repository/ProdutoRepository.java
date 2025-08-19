package com.hack.simulacao.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hack.simulacao.domain.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
}
