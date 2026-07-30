package br.com.oficina.application.port.in;

import br.com.oficina.application.command.RejeitarOrcamentoCommand;
import br.com.oficina.application.query.OrdemServicoResult;

public interface RejeitarOrcamentoInputPort {
    OrdemServicoResult execute(RejeitarOrcamentoCommand command);
}
