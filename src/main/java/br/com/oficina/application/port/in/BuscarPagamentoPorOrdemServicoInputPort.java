package br.com.oficina.application.port.in;

import br.com.oficina.application.query.PagamentoResult;

public interface BuscarPagamentoPorOrdemServicoInputPort {
    PagamentoResult executeByOrdemServico(Long ordemServicoId);
}
