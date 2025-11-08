package com.sisvendas.repository.memory;

import com.sisvendas.model.Venda;
import com.sisvendas.repository.VendaRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryVendaRepository implements VendaRepository {
    private final List<Venda> vendas = new CopyOnWriteArrayList<>();

    @Override
    public void salvar(Venda venda) {
        vendas.add(venda);
    }

    @Override
    public List<Venda> listarTodas() {
        return Collections.unmodifiableList(new ArrayList<>(vendas));
    }
}


