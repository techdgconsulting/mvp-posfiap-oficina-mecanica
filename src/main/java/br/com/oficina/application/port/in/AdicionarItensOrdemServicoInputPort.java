package br.com.oficina.application.port.in;

import br.com.oficina.application.command.AdicionarItemOSCommand;
import br.com.oficina.application.query.OrdemServicoResult;

public interface AdicionarItensOrdemServicoInputPort {
    OrdemServicoResult execute(AdicionarItemOSCommand command);
}
