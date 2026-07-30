package br.com.oficina.application.port.in;

import br.com.oficina.application.command.EnviarOrcamentoCommand;
import br.com.oficina.application.query.OrcamentoResult;

public interface EnviarOrcamentoInputPort {
    OrcamentoResult execute(EnviarOrcamentoCommand command);
}
