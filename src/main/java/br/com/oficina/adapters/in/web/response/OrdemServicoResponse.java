package br.com.oficina.adapters.in.web.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrdemServicoResponse(
    Long id,
    String numero,
    String status,
    LocalDateTime dataCriacao,
    LocalDateTime dataFinalizacao,
    String clienteNome,
    String clienteDocumento,
    String veiculoPlaca,
    String veiculoDescricao,
    List<ItemOSResponse> itens,
    BigDecimal valorTotal,
    String atendenteNome,
    String mecanicoNome
) {
    public record ItemOSResponse(
        Long id,
        String tipo,
        String descricao,
        int quantidade,
        BigDecimal valorUnitario,
        BigDecimal subtotal
    ) {}
}
