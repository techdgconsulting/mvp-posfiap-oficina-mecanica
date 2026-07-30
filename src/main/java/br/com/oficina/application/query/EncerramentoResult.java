package br.com.oficina.application.query;

import java.time.LocalDateTime;

public record EncerramentoResult(
    Long id,
    Long ordemServicoId,
    String status,
    LocalDateTime dataEncerramento
) {}
