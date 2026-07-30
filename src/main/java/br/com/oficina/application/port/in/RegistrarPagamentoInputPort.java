package br.com.oficina.application.port.in;

import br.com.oficina.application.command.RegistrarPagamentoCommand;
import br.com.oficina.application.query.OrdemServicoResult;

public interface RegistrarPagamentoInputPort {
    OrdemServicoResult execute(RegistrarPagamentoCommand command);
}
