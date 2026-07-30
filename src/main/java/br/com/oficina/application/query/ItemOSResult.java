package br.com.oficina.application.query;

import java.math.BigDecimal;

public record ItemOSResult(
    Long id,
    String tipo,
    String descricao,
    int quantidade,
    BigDecimal valorUnitario,
    BigDecimal subtotal
) {}
