package br.com.oficina.application.port.in;

import br.com.oficina.application.command.AtualizarPecaCommand;
import br.com.oficina.application.query.PecaResult;

public interface AtualizarPecaInputPort {
    PecaResult execute(AtualizarPecaCommand command);
}
