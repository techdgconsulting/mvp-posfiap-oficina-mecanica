package br.com.oficina.application.port.in;

import br.com.oficina.application.query.OrdemServicoResult;
import br.com.oficina.domain.valueobject.StatusOS;
import java.util.List;

public interface ListarOrdensServicoPorStatusInputPort {
    List<OrdemServicoResult> execute(StatusOS status);
}
