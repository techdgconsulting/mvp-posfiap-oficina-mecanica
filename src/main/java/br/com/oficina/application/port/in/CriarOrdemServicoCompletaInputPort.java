package br.com.oficina.application.port.in;

import br.com.oficina.application.command.CriarOrdemServicoCompletaCommand;
import br.com.oficina.application.query.OrdemServicoResult;

public interface CriarOrdemServicoCompletaInputPort {
    OrdemServicoResult execute(CriarOrdemServicoCompletaCommand command);
}
