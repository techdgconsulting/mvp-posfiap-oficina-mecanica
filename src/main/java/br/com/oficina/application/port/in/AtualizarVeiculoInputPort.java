package br.com.oficina.application.port.in;

import br.com.oficina.application.command.AtualizarVeiculoCommand;
import br.com.oficina.application.query.VeiculoResult;

public interface AtualizarVeiculoInputPort {
    VeiculoResult execute(AtualizarVeiculoCommand command);
}
