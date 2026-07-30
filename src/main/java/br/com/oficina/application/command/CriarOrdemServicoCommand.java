package br.com.oficina.application.command;

import java.util.List;

public record CriarOrdemServicoCommand(
    Long clienteId,
    Long veiculoId,
    List<ItemOSCommand> itens,
    String atendenteNome
) {}
