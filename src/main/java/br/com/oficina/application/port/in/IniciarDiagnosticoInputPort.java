package br.com.oficina.application.port.in;

import br.com.oficina.application.command.IniciarDiagnosticoCommand;
import br.com.oficina.application.query.OrdemServicoResult;

public interface IniciarDiagnosticoInputPort {
    OrdemServicoResult execute(IniciarDiagnosticoCommand command);
}
