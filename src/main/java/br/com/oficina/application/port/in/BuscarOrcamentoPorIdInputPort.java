package br.com.oficina.application.port.in;

import br.com.oficina.application.query.OrcamentoResult;

public interface BuscarOrcamentoPorIdInputPort {
    OrcamentoResult execute(Long id);
}
