package br.com.oficina.application.port.in;

import br.com.oficina.application.query.OrcamentoValidadeResult;

public interface ValidarValidadeOrcamentoInputPort {
    OrcamentoValidadeResult executeValidade(Long id);
}
