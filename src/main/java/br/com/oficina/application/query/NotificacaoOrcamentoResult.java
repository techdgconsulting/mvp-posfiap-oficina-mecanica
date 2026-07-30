package br.com.oficina.application.query;

import java.time.LocalDateTime;

public record NotificacaoOrcamentoResult(
        Long orcamentoId,
        Long ordemServicoId,
        String numeroOrdemServico,
        String emailDestino,
        LocalDateTime dataExpiracao,
        String linkAprovacao,
        String linkRecusa) {}
