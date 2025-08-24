package com.hack.simulacao.api.error.exceptions;

public class NenhumProdutoCadastradoException extends RuntimeException {
    public NenhumProdutoCadastradoException() {
        super("Nenhum produto cadastrado.");
    }
}
