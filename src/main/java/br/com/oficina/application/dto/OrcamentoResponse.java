package br.com.oficina.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrcamentoResponse(
    Long id,
    Long ordemServicoId,
    String status,
    BigDecimal valorTotal,
    LocalDateTime dataCriacao,
    LocalDateTime dataValidade
) {}
