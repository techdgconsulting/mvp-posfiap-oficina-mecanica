package br.com.oficina.application.port.in;

import br.com.oficina.application.command.AtualizarClienteCommand;
import br.com.oficina.application.query.ClienteResult;

public interface AtualizarClienteInputPort {
    ClienteResult execute(AtualizarClienteCommand command);
}
