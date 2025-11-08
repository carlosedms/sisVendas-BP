package com.sisvendas.exception;

public class EstoqueInsuficienteException extends RuntimeException {
    public EstoqueInsuficienteException(String codigo, int solicitado, int disponivel) {
        super("Estoque insuficiente para produto " + codigo + " (solicitado=" + solicitado + ", disponivel=" + disponivel + ")");
    }
}


