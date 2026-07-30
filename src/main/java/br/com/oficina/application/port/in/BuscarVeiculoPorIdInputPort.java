package br.com.oficina.application.port.in;

import br.com.oficina.application.query.VeiculoResult;

public interface BuscarVeiculoPorIdInputPort {
    VeiculoResult execute(Long id);
}
