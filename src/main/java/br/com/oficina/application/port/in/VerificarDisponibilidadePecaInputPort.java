package br.com.oficina.application.port.in;

import br.com.oficina.application.command.VerificarDisponibilidadePecaCommand;
import br.com.oficina.application.query.DisponibilidadePecaResult;

public interface VerificarDisponibilidadePecaInputPort {
    DisponibilidadePecaResult execute(VerificarDisponibilidadePecaCommand command);
}
