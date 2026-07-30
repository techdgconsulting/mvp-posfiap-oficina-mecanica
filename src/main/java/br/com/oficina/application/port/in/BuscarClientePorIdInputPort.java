package br.com.oficina.application.port.in;

import br.com.oficina.application.query.ClienteResult;

public interface BuscarClientePorIdInputPort {
    ClienteResult execute(Long id);
}
