package br.com.oficina.adapters.in.web.response;

import java.math.BigDecimal;

public record PecaResponse(
    Long id,
    String nome,
    String descricao,
    int quantidadeEstoque,
    BigDecimal valorUnitario,
    int estoqueMinimo,
    boolean estoqueBaixo
) {}
