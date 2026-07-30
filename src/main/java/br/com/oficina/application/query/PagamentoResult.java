package br.com.oficina.application.query;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagamentoResult(
    Long id,
    Long ordemServicoId,
    String status,
    String metodo,
    BigDecimal valor,
    LocalDateTime dataPagamento,
    String transactionId,
    String gatewayMensagem
) {}
