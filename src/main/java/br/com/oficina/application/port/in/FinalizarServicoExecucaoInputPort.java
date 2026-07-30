package br.com.oficina.application.port.in;

import br.com.oficina.application.command.FinalizarServicoExecucaoCommand;
import br.com.oficina.application.query.ExecucaoResult;

public interface FinalizarServicoExecucaoInputPort {
    ExecucaoResult execute(FinalizarServicoExecucaoCommand command);
}
