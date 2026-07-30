package br.com.oficina.application.port.in;

import br.com.oficina.application.query.ServicoResult;

public interface BuscarServicoPorIdInputPort {
    ServicoResult execute(Long id);
}
