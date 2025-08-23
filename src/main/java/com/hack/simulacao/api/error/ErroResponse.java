package com.hack.simulacao.api.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErroResponse {
    private int statusCode;
    private String message;
}
