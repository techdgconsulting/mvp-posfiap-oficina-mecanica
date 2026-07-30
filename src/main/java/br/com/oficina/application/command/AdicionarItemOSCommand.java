package br.com.oficina.application.command;

import java.util.List;

public record AdicionarItemOSCommand(Long ordemServicoId, List<ItemOSCommand> itens) {}
