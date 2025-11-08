package com.sisvendas.service;

import com.sisvendas.exception.EstoqueInsuficienteException;
import com.sisvendas.exception.ProdutoNaoEncontradoException;
import com.sisvendas.exception.ValidacaoVendaException;
import com.sisvendas.model.EnderecoEntrega;
import com.sisvendas.model.ItemVenda;
import com.sisvendas.model.Produto;
import com.sisvendas.model.TipoVenda;
import com.sisvendas.model.Venda;
import com.sisvendas.repository.ProdutoRepository;
import com.sisvendas.repository.VendaRepository;
import com.sisvendas.service.dto.ResumoPorProduto;
import com.sisvendas.service.dto.ResumoVendas;
import com.sisvendas.util.Par;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class VendaService {
    private static final Logger LOGGER = Logger.getLogger(VendaService.class.getName());

    private final ProdutoRepository produtoRepository;
    private final VendaRepository vendaRepository;
    private final Object estoqueLock = new Object();

    public VendaService(ProdutoRepository produtoRepository, VendaRepository vendaRepository) {
        this.produtoRepository = Objects.requireNonNull(produtoRepository, "produtoRepository é obrigatório");
        this.vendaRepository = Objects.requireNonNull(vendaRepository, "vendaRepository é obrigatório");
    }

    public Venda registrarVenda(TipoVenda tipo,
                                List<Par<String, Integer>> itensSolicitados,
                                Optional<EnderecoEntrega> enderecoEntrega) {
        if (tipo == null) {
            throw new ValidacaoVendaException("tipo de venda é obrigatório");
        }
        if (itensSolicitados == null || itensSolicitados.isEmpty()) {
            throw new ValidacaoVendaException("deve haver ao menos 1 item");
        }
        for (Par<String, Integer> par : itensSolicitados) {
            if (par == null || par.primeiro() == null || par.primeiro().isBlank()) {
                throw new ValidacaoVendaException("código do produto é obrigatório");
            }
            if (par.segundo() == null || par.segundo() < 1) {
                throw new ValidacaoVendaException("quantidade deve ser >= 1");
            }
        }
        if (tipo == TipoVenda.WEB && (enderecoEntrega == null || enderecoEntrega.isEmpty())) {
            throw new ValidacaoVendaException("endereço é obrigatório para vendas WEB");
        }

        Map<Produto, Integer> produtosEQuantidades = new LinkedHashMap<>();
        for (Par<String, Integer> par : itensSolicitados) {
            Produto produto = produtoRepository.buscarPorCodigo(par.primeiro())
                    .orElseThrow(() -> new ProdutoNaoEncontradoException(par.primeiro()));
            int quantidade = par.segundo();
            if (produto.getQuantidade() < quantidade) {
                throw new EstoqueInsuficienteException(produto.getCodigo(), quantidade, produto.getQuantidade());
            }
            produtosEQuantidades.merge(produto, quantidade, Integer::sum);
        }

        List<ItemVenda> itensDaVenda = new ArrayList<>();
        synchronized (estoqueLock) {
            for (Map.Entry<Produto, Integer> entry : produtosEQuantidades.entrySet()) {
                Produto p = entry.getKey();
                int qtd = entry.getValue();
                if (p.getQuantidade() < qtd) {
                    throw new EstoqueInsuficienteException(p.getCodigo(), qtd, p.getQuantidade());
                }
            }
            for (Map.Entry<Produto, Integer> entry : produtosEQuantidades.entrySet()) {
                Produto p = entry.getKey();
                int qtd = entry.getValue();
                boolean debited = p.removerQuantidade(qtd);
                if (!debited) {
                    throw new EstoqueInsuficienteException(p.getCodigo(), qtd, p.getQuantidade());
                }
                produtoRepository.atualizar(p);
                itensDaVenda.add(new ItemVenda(p, qtd));
            }
        }

        Venda venda = Venda.builder()
                .id(UUID.randomUUID().toString())
                .dataHora(LocalDateTime.now())
                .tipo(tipo)
                .itens(itensDaVenda)
                .enderecoEntrega(tipo == TipoVenda.WEB ? enderecoEntrega : Optional.empty())
                .build();

        vendaRepository.salvar(venda);
        LOGGER.log(Level.INFO, "Venda registrada com sucesso: id={0}, itens={1}, total={2}",
                new Object[]{venda.getId(), venda.getItens().size(), venda.getTotal()});
        return venda;
    }

    public List<Venda> listarVendas() {
        return vendaRepository.listarTodas().stream()
                .sorted(Comparator.comparing(Venda::getDataHora).reversed())
                .collect(Collectors.toList());
    }

    public ResumoVendas listarResumoVendas() {
        List<Venda> vendas = vendaRepository.listarTodas();
        int totalItens = vendas.stream()
                .flatMap(v -> v.getItens().stream())
                .mapToInt(ItemVenda::getQuantidade)
                .sum();
        double totalVendido = vendas.stream()
                .mapToDouble(Venda::getTotal)
                .sum();

        Map<String, ResumoPorProduto> agregados = new HashMap<>();
        for (Venda v : vendas) {
            for (ItemVenda item : v.getItens()) {
                String codigo = item.getProduto().getCodigo();
                String nome = item.getProduto().getNome();
                agregados.merge(codigo,
                        new ResumoPorProduto(codigo, nome, item.getQuantidade(), item.getSubtotal()),
                        (oldVal, newVal) -> new ResumoPorProduto(
                                oldVal.codigo(),
                                oldVal.nome(),
                                oldVal.quantidadeVendida() + newVal.quantidadeVendida(),
                                oldVal.valorTotalProduto() + newVal.valorTotalProduto()
                        ));
            }
        }

        List<ResumoPorProduto> porProduto = new ArrayList<>(agregados.values());
        porProduto.sort(Comparator.comparingInt(ResumoPorProduto::quantidadeVendida).reversed());
        return new ResumoVendas(totalItens, totalVendido, porProduto);
    }
}


