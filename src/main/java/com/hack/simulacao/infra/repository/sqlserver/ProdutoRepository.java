package com.hack.simulacao.infra.repository.sqlserver;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hack.simulacao.domain.sqlserver.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    @Cacheable("produtos")
    @Query("SELECT p FROM Produto p WHERE :valor BETWEEN p.vrMinimo AND COALESCE(p.vrMaximo, :valor) " 
    + "AND :prazo BETWEEN p.nuMinimoMeses AND COALESCE(p.nuMaximoMeses, :prazo)")
    List<Produto> findByCriterios(@Param("valor") BigDecimal valor, @Param("prazo") Integer prazo);
}
