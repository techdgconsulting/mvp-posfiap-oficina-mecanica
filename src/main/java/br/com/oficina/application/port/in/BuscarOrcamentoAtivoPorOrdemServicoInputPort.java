package br.com.oficina.application.port.in;

import br.com.oficina.application.query.OrcamentoResult;

public interface BuscarOrcamentoAtivoPorOrdemServicoInputPort {
    OrcamentoResult executeByOrdemServico(Long ordemServicoId);
}
