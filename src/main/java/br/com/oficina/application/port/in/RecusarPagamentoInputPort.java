package br.com.oficina.application.port.in;

import br.com.oficina.application.command.RecusarPagamentoCommand;
import br.com.oficina.application.query.PagamentoResult;

public interface RecusarPagamentoInputPort {
    PagamentoResult execute(RecusarPagamentoCommand command);
}
