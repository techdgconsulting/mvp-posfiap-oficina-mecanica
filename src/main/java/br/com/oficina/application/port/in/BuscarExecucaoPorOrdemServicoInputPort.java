package br.com.oficina.application.port.in;

import br.com.oficina.application.query.ExecucaoResult;

public interface BuscarExecucaoPorOrdemServicoInputPort {
    ExecucaoResult executeByOrdemServico(Long ordemServicoId);
}
