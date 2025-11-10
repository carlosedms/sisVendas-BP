package com.sisvendas.repository;

import com.sisvendas.model.Produto;
import java.util.Optional;

public interface ProdutoRepository {
    Optional<Produto> buscarPorCodigo(String codigo);
    void salvar(Produto produto);
    void atualizar(Produto produto);

    java.util.List<Produto> listarTodos();
}


