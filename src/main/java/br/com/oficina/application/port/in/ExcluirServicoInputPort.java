package br.com.oficina.application.port.in;

import br.com.oficina.application.command.ExcluirServicoCommand;

public interface ExcluirServicoInputPort {
    void execute(ExcluirServicoCommand command);
}
