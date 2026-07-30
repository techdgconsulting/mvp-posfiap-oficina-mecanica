package br.com.oficina.application.port.in;

import br.com.oficina.application.command.AprovarOrcamentoDiretoCommand;
import br.com.oficina.application.query.OrcamentoResult;

public interface AprovarOrcamentoDiretoInputPort {
    OrcamentoResult execute(AprovarOrcamentoDiretoCommand command);
}
