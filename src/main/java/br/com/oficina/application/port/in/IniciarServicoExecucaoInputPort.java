package br.com.oficina.application.port.in;

import br.com.oficina.application.command.IniciarServicoExecucaoCommand;
import br.com.oficina.application.query.ExecucaoResult;

public interface IniciarServicoExecucaoInputPort {
    ExecucaoResult execute(IniciarServicoExecucaoCommand command);
}
