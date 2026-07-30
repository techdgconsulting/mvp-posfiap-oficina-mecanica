package br.com.oficina.application.port.in;

import br.com.oficina.application.command.RejeitarOrcamentoDiretoCommand;
import br.com.oficina.application.query.OrcamentoResult;

public interface RejeitarOrcamentoDiretoInputPort {
    OrcamentoResult execute(RejeitarOrcamentoDiretoCommand command);
}
