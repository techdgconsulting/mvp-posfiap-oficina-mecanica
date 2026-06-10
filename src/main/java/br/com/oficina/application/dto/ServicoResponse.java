package br.com.oficina.application.dto;

import java.math.BigDecimal;

public record ServicoResponse(
    Long id,
    String nome,
    String descricao,
    BigDecimal valorUnitario,
    Integer tempoEstimadoMinutos
) {}
