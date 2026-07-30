package br.com.oficina.application.query;

import java.math.BigDecimal;

public record ServicoResult(
    Long id,
    String nome,
    String descricao,
    BigDecimal valorUnitario,
    Integer tempoEstimadoMinutos
) {}
