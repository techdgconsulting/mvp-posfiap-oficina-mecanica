package br.com.oficina.application.command;

import java.math.BigDecimal;

public record AtualizarServicoCommand(
    Long id,
    String nome,
    String descricao,
    BigDecimal valorUnitario,
    Integer tempoEstimadoMinutos
) {}
