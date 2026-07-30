package br.com.oficina.application.port.in;

import br.com.oficina.application.query.OrdemServicoResult;

public interface BuscarOrdemServicoPorIdInputPort {
    OrdemServicoResult execute(Long id);
}
