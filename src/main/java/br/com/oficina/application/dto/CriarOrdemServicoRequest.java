package br.com.oficina.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CriarOrdemServicoRequest(
    @NotNull(message = "ID do cliente é obrigatório")
    Long clienteId,

    @NotNull(message = "ID do veículo é obrigatório")
    Long veiculoId,

    @Valid
    List<ItemOSRequest> itens
) {}
