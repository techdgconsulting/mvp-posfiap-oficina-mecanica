package br.com.oficina.application.port.in;

import br.com.oficina.application.command.ExcluirClienteCommand;

public interface ExcluirClienteInputPort {
    void execute(ExcluirClienteCommand command);
}
