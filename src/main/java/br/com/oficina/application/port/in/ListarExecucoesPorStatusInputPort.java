package br.com.oficina.application.port.in;

import br.com.oficina.application.query.ExecucaoResult;
import br.com.oficina.domain.valueobject.StatusExecucao;
import java.util.List;

public interface ListarExecucoesPorStatusInputPort {
    List<ExecucaoResult> execute(StatusExecucao status);
}
