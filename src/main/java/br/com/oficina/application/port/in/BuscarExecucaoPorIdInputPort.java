package br.com.oficina.application.port.in;

import br.com.oficina.application.query.ExecucaoResult;

public interface BuscarExecucaoPorIdInputPort {
    ExecucaoResult execute(Long id);
}
