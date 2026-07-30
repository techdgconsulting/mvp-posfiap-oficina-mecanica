package br.com.oficina.application.port.in;

import br.com.oficina.application.command.RegistrarEntregaVeiculoCommand;
import br.com.oficina.application.query.EntregaResult;

public interface RegistrarEntregaVeiculoInputPort {
    EntregaResult execute(RegistrarEntregaVeiculoCommand command);
}
