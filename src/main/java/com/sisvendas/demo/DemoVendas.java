package com.sisvendas.demo;

import com.sisvendas.model.EnderecoEntrega;
import com.sisvendas.model.Produto;
import com.sisvendas.model.TipoVenda;
import com.sisvendas.repository.memory.InMemoryProdutoRepository;
import com.sisvendas.repository.memory.InMemoryVendaRepository;
import com.sisvendas.service.VendaService;
import com.sisvendas.service.dto.ResumoVendas;
import com.sisvendas.util.Par;
import com.sisvendas.view.ConsoleVendasPresenter;
import java.util.List;
import java.util.Optional;

public class DemoVendas {
    public static void main(String[] args) {
        InMemoryProdutoRepository prodRepo = new InMemoryProdutoRepository();
        prodRepo.salvar(new Produto("001", "Caneta", 2.5, 100));
        prodRepo.salvar(new Produto("002", "Caderno", 15.0, 50));
        prodRepo.salvar(new Produto("003", "Borracha", 1.75, 80));

        InMemoryVendaRepository vendaRepo = new InMemoryVendaRepository();
        VendaService service = new VendaService(prodRepo, vendaRepo);
        ConsoleVendasPresenter presenter = new ConsoleVendasPresenter();

        service.registrarVenda(
                TipoVenda.LOJA,
                List.of(new Par<>("001", 2), new Par<>("002", 1)),
                Optional.empty()
        );

        EnderecoEntrega endereco = new EnderecoEntrega("Cliente X","Rua A","123","Centro","Natal","RN","59000-000");
        service.registrarVenda(
                TipoVenda.WEB,
                List.of(new Par<>("001", 1)),
                Optional.of(endereco)
        );

        service.registrarVenda(
                TipoVenda.LOJA,
                List.of(new Par<>("002", 3)),
                Optional.empty()
        );

        presenter.imprimirListaVendas(service.listarVendas());

        ResumoVendas resumo = service.listarResumoVendas();
        presenter.imprimirResumoVendas(resumo);

        List<Produto> estoque = service.listarEstoque();
        presenter.imprimirRelatorioEstoque(estoque);
    }
}


