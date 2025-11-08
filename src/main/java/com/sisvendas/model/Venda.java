package com.sisvendas.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class Venda {
    private final String id;
    private final LocalDateTime dataHora;
    private final TipoVenda tipo;
    private final List<ItemVenda> itens;
    private final double total;
    private final Optional<EnderecoEntrega> enderecoEntrega;

    private Venda(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.dataHora = builder.dataHora != null ? builder.dataHora : LocalDateTime.now();
        this.tipo = Objects.requireNonNull(builder.tipo, "tipo é obrigatório");
        if (builder.itens == null || builder.itens.isEmpty()) {
            throw new IllegalArgumentException("deve haver ao menos 1 item");
        }
        this.itens = Collections.unmodifiableList(new ArrayList<>(builder.itens));
        this.total = this.itens.stream().mapToDouble(ItemVenda::getSubtotal).sum();
        if (this.tipo == TipoVenda.WEB) {
            this.enderecoEntrega = Optional.of(
                    Objects.requireNonNull(builder.enderecoEntrega.orElse(null), "endereço é obrigatório para WEB"));
        } else {
            this.enderecoEntrega = Optional.empty();
        }
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public TipoVenda getTipo() {
        return tipo;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public double getTotal() {
        return total;
    }

    public Optional<EnderecoEntrega> getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private LocalDateTime dataHora;
        private TipoVenda tipo;
        private List<ItemVenda> itens;
        private Optional<EnderecoEntrega> enderecoEntrega = Optional.empty();

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder dataHora(LocalDateTime dataHora) {
            this.dataHora = dataHora;
            return this;
        }

        public Builder tipo(TipoVenda tipo) {
            this.tipo = tipo;
            return this;
        }

        public Builder itens(List<ItemVenda> itens) {
            this.itens = itens;
            return this;
        }

        public Builder enderecoEntrega(Optional<EnderecoEntrega> enderecoEntrega) {
            this.enderecoEntrega = enderecoEntrega;
            return this;
        }

        public Venda build() {
            return new Venda(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Venda)) return false;
        Venda venda = (Venda) o;
        return id.equals(venda.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}


