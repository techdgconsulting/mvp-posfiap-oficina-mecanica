package br.com.oficina.application.port.in;

import br.com.oficina.application.query.EntregaResult;

public interface BuscarEntregaPorOrdemServicoInputPort {
    EntregaResult executeByOrdemServico(Long ordemServicoId);
}
