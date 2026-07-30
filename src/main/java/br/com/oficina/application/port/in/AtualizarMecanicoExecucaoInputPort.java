package br.com.oficina.application.port.in;

import br.com.oficina.application.command.AtualizarMecanicoExecucaoCommand;
import br.com.oficina.application.query.ExecucaoResult;

public interface AtualizarMecanicoExecucaoInputPort {
    ExecucaoResult execute(AtualizarMecanicoExecucaoCommand command);
}
