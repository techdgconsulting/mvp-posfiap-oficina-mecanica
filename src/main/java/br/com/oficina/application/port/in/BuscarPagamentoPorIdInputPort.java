package br.com.oficina.application.port.in;

import br.com.oficina.application.query.PagamentoResult;

public interface BuscarPagamentoPorIdInputPort {
    PagamentoResult execute(Long id);
}
