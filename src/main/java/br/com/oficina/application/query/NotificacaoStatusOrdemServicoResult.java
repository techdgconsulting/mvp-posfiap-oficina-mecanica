package br.com.oficina.application.query;

public record NotificacaoStatusOrdemServicoResult(
        Long ordemServicoId,
        String numeroOS,
        String status,
        String emailDestino,
        boolean enviado,
        String mensagem
) {}
