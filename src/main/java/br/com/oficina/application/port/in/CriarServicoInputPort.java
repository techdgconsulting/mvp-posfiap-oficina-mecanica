package br.com.oficina.application.port.in;

import br.com.oficina.application.command.CriarServicoCommand;
import br.com.oficina.application.query.ServicoResult;

public interface CriarServicoInputPort {
    ServicoResult execute(CriarServicoCommand command);
}
