package br.com.oficina.application.command;

public record RegistrarPagamentoCommand(Long ordemServicoId, String metodoPagamento) {}
