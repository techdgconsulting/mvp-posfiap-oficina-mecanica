package br.com.oficina.application.port.in;

import br.com.oficina.application.command.ExcluirVeiculoCommand;

public interface ExcluirVeiculoInputPort {
    void execute(ExcluirVeiculoCommand command);
}
