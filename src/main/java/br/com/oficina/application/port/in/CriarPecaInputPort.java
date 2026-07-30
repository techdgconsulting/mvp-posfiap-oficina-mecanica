package br.com.oficina.application.port.in;

import br.com.oficina.application.command.CriarPecaCommand;
import br.com.oficina.application.query.PecaResult;

public interface CriarPecaInputPort {
    PecaResult execute(CriarPecaCommand command);
}
