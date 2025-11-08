package com.sisvendas.view;

import com.sisvendas.model.ItemVenda;
import com.sisvendas.model.Venda;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConsoleVendasPresenter {
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void imprimirVenda(Venda v) {
        StringBuilder sb = new StringBuilder();
        sb.append("Venda ").append(v.getId()).append(" - ").append(DF.format(v.getDataHora())).append('\n');
        sb.append("Tipo: ").append(v.getTipo()).append('\n');
        v.getEnderecoEntrega().ifPresent(e -> sb.append("Entrega: ")
                .append(e.getDestinatario()).append(", ")
                .append(e.getLogradouro()).append(", ")
                .append(e.getNumero()).append(" - ")
                .append(e.getBairro()).append(", ")
                .append(e.getCidade()).append("/").append(e.getUf())
                .append(" CEP ").append(e.getCep()).append('\n'));
        sb.append("Itens:\n");
        for (ItemVenda item : v.getItens()) {
            sb.append(" - ").append(item.getProduto().getCodigo()).append(" | ")
                    .append(item.getProduto().getNome()).append(" | qtd=")
                    .append(item.getQuantidade()).append(" | subtotal=R$ ")
                    .append(String.format("%.2f", item.getSubtotal())).append('\n');
        }
        sb.append("Total: R$ ").append(String.format("%.2f", v.getTotal())).append('\n');
        System.out.print(sb.toString());
    }

    public void imprimirListaVendas(List<Venda> vendas) {
        if (vendas == null || vendas.isEmpty()) {
            System.out.println("Sem vendas para exibir.");
            return;
        }
        for (Venda v : vendas) {
            imprimirVenda(v);
            System.out.println("-----------------------------");
        }
    }
}


