package br.com.oficina.application.port.in;

import br.com.oficina.application.command.CriarPagamentoCommand;
import br.com.oficina.application.query.PagamentoResult;

public interface CriarPagamentoInputPort {
    PagamentoResult execute(CriarPagamentoCommand command);
}
