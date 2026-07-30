package br.com.oficina.application.port.in;

import br.com.oficina.application.command.RegistrarDiagnosticoExecucaoCommand;
import br.com.oficina.application.query.ExecucaoResult;

public interface RegistrarDiagnosticoExecucaoInputPort {
    ExecucaoResult execute(RegistrarDiagnosticoExecucaoCommand command);
}
