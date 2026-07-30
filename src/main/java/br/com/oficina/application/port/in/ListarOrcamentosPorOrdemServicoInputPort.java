package br.com.oficina.application.port.in;

import br.com.oficina.application.query.OrcamentoResult;
import java.util.List;

public interface ListarOrcamentosPorOrdemServicoInputPort {
    List<OrcamentoResult> executeByOrdemServico(Long ordemServicoId);
}
