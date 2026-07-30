package br.com.oficina.application.command;

import java.math.BigDecimal;

public record CriarPagamentoCommand(Long ordemServicoId, BigDecimal valor, String metodoPagamento) {}
