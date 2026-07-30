package br.com.oficina.adapters.in.web.response;

import java.math.BigDecimal;

public record ServicoResponse(
    Long id,
    String nome,
    String descricao,
    BigDecimal valorUnitario,
    Integer tempoEstimadoMinutos
) {}
