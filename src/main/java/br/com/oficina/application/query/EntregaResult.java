package br.com.oficina.application.query;

import java.time.LocalDateTime;

public record EntregaResult(
    Long id,
    Long ordemServicoId,
    String status,
    LocalDateTime dataEntrega,
    String observacoes
) {}
