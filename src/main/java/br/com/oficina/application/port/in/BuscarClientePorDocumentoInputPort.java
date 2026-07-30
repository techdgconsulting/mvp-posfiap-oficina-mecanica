package br.com.oficina.application.port.in;

import br.com.oficina.application.query.ClienteResult;

public interface BuscarClientePorDocumentoInputPort {
    ClienteResult execute(String documento);
}
