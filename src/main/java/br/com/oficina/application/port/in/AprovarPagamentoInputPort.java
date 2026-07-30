package br.com.oficina.application.port.in;

import br.com.oficina.application.command.AprovarPagamentoCommand;
import br.com.oficina.application.query.PagamentoResult;

public interface AprovarPagamentoInputPort {
    PagamentoResult execute(AprovarPagamentoCommand command);
}
