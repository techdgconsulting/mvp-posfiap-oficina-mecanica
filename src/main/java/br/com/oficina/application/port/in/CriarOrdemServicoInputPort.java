package br.com.oficina.application.port.in;

import br.com.oficina.application.command.CriarOrdemServicoCommand;
import br.com.oficina.application.query.OrdemServicoResult;

public interface CriarOrdemServicoInputPort {
    OrdemServicoResult execute(CriarOrdemServicoCommand command);
}
