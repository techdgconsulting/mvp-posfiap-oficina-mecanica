package br.com.oficina.application.port.in;

import br.com.oficina.application.command.EncerrarOrdemServicoCommand;
import br.com.oficina.application.query.EncerramentoResult;

public interface EncerrarOrdemServicoInputPort {
    EncerramentoResult execute(EncerrarOrdemServicoCommand command);
}
