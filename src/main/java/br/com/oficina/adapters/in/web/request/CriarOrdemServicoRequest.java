package br.com.oficina.adapters.in.web.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CriarOrdemServicoRequest(
    @NotNull(message = "ID do cliente e obrigatorio")
    Long clienteId,

    @NotNull(message = "ID do veiculo e obrigatorio")
    Long veiculoId,

    @Valid
    List<ItemOSRequest> itens
) {}
