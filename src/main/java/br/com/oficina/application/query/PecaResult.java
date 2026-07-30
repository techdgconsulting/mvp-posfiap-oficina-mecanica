package br.com.oficina.application.query;

import java.math.BigDecimal;

public record PecaResult(
    Long id,
    String nome,
    String descricao,
    int quantidadeEstoque,
    BigDecimal valorUnitario,
    int estoqueMinimo,
    boolean estoqueBaixo
) {}
