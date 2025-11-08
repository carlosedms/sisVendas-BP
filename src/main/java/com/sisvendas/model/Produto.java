package com.sisvendas.model;

import java.util.Objects;

public class Produto {
    private final String codigo;
    private String nome;
    private double preco;
    private int quantidade;

    public Produto(String codigo, String nome, double preco, int quantidadeInicial) {
        this.codigo = Objects.requireNonNull(codigo, "codigo é obrigatório");
        this.nome = Objects.requireNonNull(nome, "nome é obrigatório");
        if (preco < 0) {
            throw new IllegalArgumentException("preço não pode ser negativo");
        }
        this.preco = preco;
        if (quantidadeInicial < 0) {
            this.quantidade = 0;
        } else {
            this.quantidade = quantidadeInicial;
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setNome(String nome) {
        this.nome = Objects.requireNonNull(nome, "nome é obrigatório");
    }

    public void setPreco(double preco) {
        if (preco < 0) {
            throw new IllegalArgumentException("preço não pode ser negativo");
        }
        this.preco = preco;
    }

    public void adicionarQuantidade(int quantidadeAdicional) {
        if (quantidadeAdicional <= 0) {
            throw new IllegalArgumentException("quantidade de entrada deve ser positiva");
        }
        this.quantidade += quantidadeAdicional;
    }

    public boolean removerQuantidade(int quantidadeARemover) {
        if (quantidadeARemover <= 0) {
            return false;
        }
        if (this.quantidade >= quantidadeARemover) {
            this.quantidade -= quantidadeARemover;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Produto [" +
                "Código: '" + codigo + '\'' +
                ", Nome: '" + nome + '\'' +
                ", Preço: R$ " + String.format("%.2f", preco) +
                ", Estoque: " + quantidade + " unidades" +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Produto)) return false;
        Produto produto = (Produto) o;
        return codigo.equals(produto.codigo);
    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }
}


