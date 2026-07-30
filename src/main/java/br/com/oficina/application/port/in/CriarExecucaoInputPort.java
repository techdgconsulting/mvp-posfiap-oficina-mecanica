package br.com.oficina.application.port.in;

import br.com.oficina.application.command.CriarExecucaoCommand;
import br.com.oficina.application.query.ExecucaoResult;

public interface CriarExecucaoInputPort {
    ExecucaoResult execute(CriarExecucaoCommand command);
}
