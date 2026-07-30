package br.com.oficina.application.port.in;

import br.com.oficina.application.query.EntregaResult;

public interface BuscarEntregaPorIdInputPort {
    EntregaResult execute(Long id);
}
