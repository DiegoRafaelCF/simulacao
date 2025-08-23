package com.hack.simulacao.infra.repository.sqlserver;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hack.simulacao.domain.sqlserver.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    @Override
    @Cacheable("produtos")
    List<Produto> findAll();
}
