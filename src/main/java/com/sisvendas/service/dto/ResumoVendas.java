package com.sisvendas.service.dto;

import java.util.List;

public record ResumoVendas(int totalItensVendidos, double valorTotalVendido, List<ResumoPorProduto> porProduto) { }


