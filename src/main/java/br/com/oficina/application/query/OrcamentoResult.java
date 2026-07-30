package br.com.oficina.application.query;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrcamentoResult(
    Long id,
    Long ordemServicoId,
    String status,
    BigDecimal valorTotal,
    LocalDateTime dataCriacao,
    LocalDateTime dataValidade
) {}
