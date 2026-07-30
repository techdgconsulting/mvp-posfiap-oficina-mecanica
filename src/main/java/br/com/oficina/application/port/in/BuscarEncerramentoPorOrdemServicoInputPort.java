package br.com.oficina.application.port.in;

import br.com.oficina.application.query.EncerramentoResult;

public interface BuscarEncerramentoPorOrdemServicoInputPort {
    EncerramentoResult executeByOrdemServico(Long ordemServicoId);
}
