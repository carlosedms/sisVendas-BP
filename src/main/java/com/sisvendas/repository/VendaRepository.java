package com.sisvendas.repository;

import com.sisvendas.model.Venda;
import java.util.List;

public interface VendaRepository {
    void salvar(Venda venda);
    List<Venda> listarTodas();
}


