package com.hack.simulacao.api.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultadoSimulacao {
    private String tipo;
    private List<ParcelaResponse> parcelas;
}
