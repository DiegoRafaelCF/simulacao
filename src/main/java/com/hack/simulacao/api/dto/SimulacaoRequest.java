package com.hack.simulacao.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record SimulacaoRequest (
    @NotNull(message = "O campo 'valorDesejado' é obrigatório")
    @DecimalMin(value = "200.00", message = "O valor mínimo para o campo 'valorDesejado' é 200.00")
    BigDecimal valorDesejado,

    @NotNull(message = "O campo 'prazo' é obrigatório")
    @PositiveOrZero(message = "O campo 'prazo' deve ser maior ou igual a 0")
    Integer prazo
) {}
