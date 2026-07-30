package br.com.oficina.application.port.in;

import br.com.oficina.application.command.EntregarVeiculoCommand;
import br.com.oficina.application.query.OrdemServicoResult;

public interface EntregarVeiculoInputPort {
    OrdemServicoResult execute(EntregarVeiculoCommand command);
}
