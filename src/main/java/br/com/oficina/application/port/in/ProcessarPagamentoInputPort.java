package br.com.oficina.application.port.in;

import br.com.oficina.application.command.ProcessarPagamentoCommand;
import br.com.oficina.application.query.PagamentoResult;

public interface ProcessarPagamentoInputPort {
    PagamentoResult execute(ProcessarPagamentoCommand command);
}
