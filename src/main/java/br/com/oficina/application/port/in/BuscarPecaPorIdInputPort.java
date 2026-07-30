package br.com.oficina.application.port.in;

import br.com.oficina.application.query.PecaResult;

public interface BuscarPecaPorIdInputPort {
    PecaResult execute(Long id);
}
