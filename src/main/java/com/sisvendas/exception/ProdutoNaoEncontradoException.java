package com.sisvendas.exception;

public class ProdutoNaoEncontradoException extends RuntimeException {
    public ProdutoNaoEncontradoException(String codigo) {
        super("Produto não encontrado: " + codigo);
    }
}


