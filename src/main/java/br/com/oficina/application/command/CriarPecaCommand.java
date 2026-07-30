package br.com.oficina.application.command;

import java.math.BigDecimal;

public record CriarPecaCommand(
    String nome,
    String descricao,
    Integer quantidadeEstoque,
    BigDecimal valorUnitario,
    Integer estoqueMinimo
) {}
