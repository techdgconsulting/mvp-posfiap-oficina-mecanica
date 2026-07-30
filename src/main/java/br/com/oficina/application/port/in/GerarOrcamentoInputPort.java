package br.com.oficina.application.port.in;

import br.com.oficina.application.command.GerarOrcamentoCommand;
import br.com.oficina.application.query.OrcamentoResult;

public interface GerarOrcamentoInputPort {
    OrcamentoResult execute(GerarOrcamentoCommand command);
}
