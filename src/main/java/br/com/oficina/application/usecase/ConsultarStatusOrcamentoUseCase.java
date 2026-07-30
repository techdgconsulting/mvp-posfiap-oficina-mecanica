package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.ConsultarStatusOrcamentoInputPort;
import br.com.oficina.application.port.out.OrcamentoRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConsultarStatusOrcamentoUseCase implements ConsultarStatusOrcamentoInputPort {

    private final OrcamentoRepositoryPort orcamentoRepository;

    @Override
    public String executeStatus(Long id) {
        return orcamentoRepository.buscarPorId(id)
                .map(orcamento -> orcamento.getStatus().name())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Orcamento nao encontrado: " + id));
    }
}
