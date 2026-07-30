package br.com.oficina.application.port.in;

import br.com.oficina.application.command.ExcluirPecaCommand;

public interface ExcluirPecaInputPort {
    void execute(ExcluirPecaCommand command);
}
