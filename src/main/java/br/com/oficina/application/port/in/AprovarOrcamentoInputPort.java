package br.com.oficina.application.port.in;

import br.com.oficina.application.command.AprovarOrcamentoCommand;
import br.com.oficina.application.query.OrdemServicoResult;

public interface AprovarOrcamentoInputPort {
    OrdemServicoResult execute(AprovarOrcamentoCommand command);
}
