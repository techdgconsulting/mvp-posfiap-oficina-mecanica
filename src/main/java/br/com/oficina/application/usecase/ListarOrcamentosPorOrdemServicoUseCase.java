package br.com.oficina.application.usecase;

import br.com.oficina.application.port.in.ListarOrcamentosPorOrdemServicoInputPort;
import br.com.oficina.application.port.out.OrcamentoRepositoryPort;
import br.com.oficina.application.query.OrcamentoResult;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListarOrcamentosPorOrdemServicoUseCase implements ListarOrcamentosPorOrdemServicoInputPort {

    private final OrcamentoRepositoryPort orcamentoRepository;

    @Override
    public List<OrcamentoResult> executeByOrdemServico(Long ordemServicoId) {
        return orcamentoRepository.listarPorOrdemDeServico(ordemServicoId).stream()
                .map(OrcamentoResultMapper::toResult)
                .toList();
    }
}
