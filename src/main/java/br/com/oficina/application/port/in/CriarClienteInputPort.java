package br.com.oficina.application.port.in;

import br.com.oficina.application.command.CriarClienteCommand;
import br.com.oficina.application.query.ClienteResult;

public interface CriarClienteInputPort {
    ClienteResult execute(CriarClienteCommand command);
}
