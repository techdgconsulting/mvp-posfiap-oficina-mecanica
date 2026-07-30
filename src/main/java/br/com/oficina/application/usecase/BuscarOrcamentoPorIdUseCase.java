package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.BuscarOrcamentoPorIdInputPort;
import br.com.oficina.application.port.out.OrcamentoRepositoryPort;
import br.com.oficina.application.query.OrcamentoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuscarOrcamentoPorIdUseCase implements BuscarOrcamentoPorIdInputPort {

    private final OrcamentoRepositoryPort orcamentoRepository;

    @Override
    public OrcamentoResult execute(Long id) {
        return orcamentoRepository.buscarPorId(id)
                .map(OrcamentoResultMapper::toResult)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Orcamento nao encontrado: " + id));
    }
}
