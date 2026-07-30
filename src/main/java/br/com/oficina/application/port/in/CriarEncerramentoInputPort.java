package br.com.oficina.application.port.in;

import br.com.oficina.application.command.CriarEncerramentoCommand;
import br.com.oficina.application.query.EncerramentoResult;

public interface CriarEncerramentoInputPort {
    EncerramentoResult execute(CriarEncerramentoCommand command);
}
