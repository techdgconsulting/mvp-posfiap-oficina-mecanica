package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.BuscarOrcamentoAtivoPorOrdemServicoInputPort;
import br.com.oficina.application.port.out.OrcamentoRepositoryPort;
import br.com.oficina.application.query.OrcamentoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuscarOrcamentoAtivoPorOrdemServicoUseCase implements BuscarOrcamentoAtivoPorOrdemServicoInputPort {

    private final OrcamentoRepositoryPort orcamentoRepository;

    @Override
    public OrcamentoResult executeByOrdemServico(Long ordemServicoId) {
        return orcamentoRepository.buscarAtivoByOrdemDeServico(ordemServicoId)
                .map(OrcamentoResultMapper::toResult)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Orcamento ativo nao encontrado para OS: " + ordemServicoId));
    }
}
