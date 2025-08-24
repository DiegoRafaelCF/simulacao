package com.hack.simulacao.api.error.exceptions;

public class ProdutoNaoEncontradoException extends RuntimeException {
    public ProdutoNaoEncontradoException(String msg) {
        super(msg);
    }
}
