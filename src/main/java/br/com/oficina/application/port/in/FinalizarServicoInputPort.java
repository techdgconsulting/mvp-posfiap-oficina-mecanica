package br.com.oficina.application.port.in;

import br.com.oficina.application.command.FinalizarServicoCommand;
import br.com.oficina.application.query.OrdemServicoResult;

public interface FinalizarServicoInputPort {
    OrdemServicoResult execute(FinalizarServicoCommand command);
}
