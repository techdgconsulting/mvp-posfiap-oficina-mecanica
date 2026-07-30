package br.com.oficina.application.query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrdemServicoResult(
    Long id,
    String numero,
    String status,
    LocalDateTime dataCriacao,
    LocalDateTime dataFinalizacao,
    String clienteNome,
    String clienteDocumento,
    String veiculoPlaca,
    String veiculoDescricao,
    List<ItemOSResult> itens,
    BigDecimal valorTotal,
    String atendenteNome,
    String mecanicoNome
) {}
