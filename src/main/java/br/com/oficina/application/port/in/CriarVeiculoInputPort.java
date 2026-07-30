package br.com.oficina.application.port.in;

import br.com.oficina.application.command.CriarVeiculoCommand;
import br.com.oficina.application.query.VeiculoResult;

public interface CriarVeiculoInputPort {
    VeiculoResult execute(CriarVeiculoCommand command);
}
