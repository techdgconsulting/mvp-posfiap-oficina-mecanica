package br.com.oficina.application.command;

import java.math.BigDecimal;

public record CriarServicoCommand(
    String nome,
    String descricao,
    BigDecimal valorUnitario,
    Integer tempoEstimadoMinutos
) {}
