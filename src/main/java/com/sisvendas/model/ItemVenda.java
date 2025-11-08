package com.sisvendas.model;

import java.util.Objects;

public final class ItemVenda {
    private final Produto produto;
    private final int quantidade;
    private final double subtotal;

    public ItemVenda(Produto produto, int quantidade) {
        this.produto = Objects.requireNonNull(produto, "produto é obrigatório");
        if (quantidade < 1) {
            throw new IllegalArgumentException("quantidade deve ser >= 1");
        }
        this.quantidade = quantidade;
        this.subtotal = produto.getPreco() * quantidade;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getSubtotal() {
        return subtotal;
    }
}


