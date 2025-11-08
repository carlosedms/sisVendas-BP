package com.sisvendas.model;

import java.util.Objects;

public final class EnderecoEntrega {
    private final String destinatario;
    private final String logradouro;
    private final String numero;
    private final String bairro;
    private final String cidade;
    private final String uf;
    private final String cep;

    public EnderecoEntrega(String destinatario,
                           String logradouro,
                           String numero,
                           String bairro,
                           String cidade,
                           String uf,
                           String cep) {
        this.destinatario = validar(destinatario, "destinatário");
        this.logradouro = validar(logradouro, "logradouro");
        this.numero = validar(numero, "número");
        this.bairro = validar(bairro, "bairro");
        this.cidade = validar(cidade, "cidade");
        this.uf = validar(uf, "UF");
        this.cep = validar(cep, "CEP");
    }

    private static String validar(String valor, String campo) {
        Objects.requireNonNull(valor, campo + " é obrigatório");
        String v = valor.trim();
        if (v.isEmpty()) {
            throw new IllegalArgumentException(campo + " não pode ser vazio");
        }
        return v;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public String getBairro() {
        return bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public String getUf() {
        return uf;
    }

    public String getCep() {
        return cep;
    }
}


