package br.com.oficina.adapters.in.web.response;

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
