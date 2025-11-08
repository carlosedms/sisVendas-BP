package com.sisvendas.service;

import com.sisvendas.exception.EstoqueInsuficienteException;
import com.sisvendas.exception.ProdutoNaoEncontradoException;
import com.sisvendas.model.EnderecoEntrega;
import com.sisvendas.model.Produto;
import com.sisvendas.model.TipoVenda;
import com.sisvendas.model.Venda;
import com.sisvendas.repository.memory.InMemoryProdutoRepository;
import com.sisvendas.repository.memory.InMemoryVendaRepository;
import com.sisvendas.service.dto.ResumoVendas;
import com.sisvendas.util.Par;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class VendaServiceTest {
    private InMemoryProdutoRepository produtoRepo;
    private InMemoryVendaRepository vendaRepo;
    private VendaService service;

    @BeforeEach
    void setup() {
        produtoRepo = new InMemoryProdutoRepository();
        vendaRepo = new InMemoryVendaRepository();
        service = new VendaService(produtoRepo, vendaRepo);
        produtoRepo.salvar(new Produto("001", "Caneta", 2.5, 100));
        produtoRepo.salvar(new Produto("002", "Caderno", 15.0, 50));
        produtoRepo.salvar(new Produto("003", "Borracha", 1.5, 20));
    }

    @Test
    void deveRegistrarVendaLojaComMultiplosItens() {
        Venda v = service.registrarVenda(
                TipoVenda.LOJA,
                List.of(new Par<>("001", 2), new Par<>("002", 1)),
                Optional.empty()
        );
        assertNotNull(v.getId());
        assertEquals(2, v.getItens().size());
        assertEquals(2 * 2.5 + 1 * 15.0, v.getTotal(), 0.0001);
        assertEquals(98, produtoRepo.buscarPorCodigo("001").get().getQuantidade());
        assertEquals(49, produtoRepo.buscarPorCodigo("002").get().getQuantidade());
    }

    @Test
    void deveRegistrarVendaWebComEnderecoValido() {
        EnderecoEntrega end = new EnderecoEntrega("Cliente X","Rua A","123","Centro","Natal","RN","59000-000");
        Venda v = service.registrarVenda(
                TipoVenda.WEB,
                List.of(new Par<>("003", 2)),
                Optional.of(end)
        );
        assertTrue(v.getEnderecoEntrega().isPresent());
        assertEquals(18, produtoRepo.buscarPorCodigo("003").get().getQuantidade());
    }

    @Test
    void deveLancarQuandoProdutoInexistente() {
        assertThrows(ProdutoNaoEncontradoException.class, () ->
                service.registrarVenda(TipoVenda.LOJA, List.of(new Par<>("999", 1)), Optional.empty()));
    }

    @Test
    void naoDeveDebitarQuandoEstoqueInsuficiente() {
        int estoqueAntes = produtoRepo.buscarPorCodigo("002").get().getQuantidade();
        assertThrows(EstoqueInsuficienteException.class, () ->
                service.registrarVenda(TipoVenda.LOJA, List.of(new Par<>("002", estoqueAntes + 1)), Optional.empty()));
        assertEquals(estoqueAntes, produtoRepo.buscarPorCodigo("002").get().getQuantidade());
    }

    @Test
    void listarVendasOrdenadoPorDataDesc() throws InterruptedException {
        service.registrarVenda(TipoVenda.LOJA, List.of(new Par<>("001", 1)), Optional.empty());
        Thread.sleep(5); // garante ordem temporal
        Venda v2 = service.registrarVenda(TipoVenda.LOJA, List.of(new Par<>("002", 1)), Optional.empty());
        List<Venda> ordenadas = service.listarVendas();
        assertEquals(v2.getId(), ordenadas.get(0).getId());
    }

    @Test
    void resumoAgregadoPorProduto() {
        service.registrarVenda(TipoVenda.LOJA, List.of(new Par<>("001", 3)), Optional.empty());
        service.registrarVenda(TipoVenda.LOJA, List.of(new Par<>("002", 1)), Optional.empty());
        service.registrarVenda(TipoVenda.LOJA, List.of(new Par<>("001", 2)), Optional.empty());

        ResumoVendas resumo = service.listarResumoVendas();
        assertEquals(6, resumo.totalItensVendidos());
        assertEquals("001", resumo.porProduto().get(0).codigo());
        assertEquals(5, resumo.porProduto().get(0).quantidadeVendida());
    }
}


