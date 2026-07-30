package br.com.oficina.application.command;

public record AprovarPagamentoCommand(Long pagamentoId, String transactionId, String gatewayMensagem) {}
