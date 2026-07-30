package br.com.oficina.application.port.in;

import br.com.oficina.application.command.CriarEntregaCommand;
import br.com.oficina.application.query.EntregaResult;

public interface CriarEntregaInputPort {
    EntregaResult execute(CriarEntregaCommand command);
}
