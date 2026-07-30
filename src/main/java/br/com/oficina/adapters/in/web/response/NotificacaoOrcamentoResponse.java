package br.com.oficina.adapters.in.web.response;

import java.time.LocalDateTime;

public record NotificacaoOrcamentoResponse(
        Long orcamentoId,
        Long ordemServicoId,
        String numeroOrdemServico,
        String emailDestino,
        LocalDateTime dataExpiracao,
        String linkAprovacao,
        String linkRecusa) {}
