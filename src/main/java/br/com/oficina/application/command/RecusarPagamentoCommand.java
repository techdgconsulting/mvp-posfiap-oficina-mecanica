package br.com.oficina.application.command;

public record RecusarPagamentoCommand(Long pagamentoId, String transactionId, String gatewayMensagem) {}
