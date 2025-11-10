package com.sisvendas.repository.memory;

import com.sisvendas.model.Produto;
import com.sisvendas.repository.ProdutoRepository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryProdutoRepository implements ProdutoRepository {
    private final Map<String, Produto> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<Produto> buscarPorCodigo(String codigo) {
        if (codigo == null) return Optional.empty();
        return Optional.ofNullable(storage.get(codigo));
    }

    @Override
    public void salvar(Produto produto) {
        storage.put(produto.getCodigo(), produto);
    }

    @Override
    public void atualizar(Produto produto) {
        storage.put(produto.getCodigo(), produto);
    }

    @Override
    public java.util.List<Produto> listarTodos() {
        return java.util.Collections.unmodifiableList(new java.util.ArrayList<>(storage.values()));
    }
}


