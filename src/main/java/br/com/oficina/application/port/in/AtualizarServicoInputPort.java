package br.com.oficina.application.port.in;

import br.com.oficina.application.command.AtualizarServicoCommand;
import br.com.oficina.application.query.ServicoResult;

public interface AtualizarServicoInputPort {
    ServicoResult execute(AtualizarServicoCommand command);
}
