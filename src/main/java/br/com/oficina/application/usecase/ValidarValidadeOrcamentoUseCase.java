package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.ValidarValidadeOrcamentoInputPort;
import br.com.oficina.application.port.out.OrcamentoRepositoryPort;
import br.com.oficina.application.query.OrcamentoValidadeResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidarValidadeOrcamentoUseCase implements ValidarValidadeOrcamentoInputPort {

    private final OrcamentoRepositoryPort orcamentoRepository;

    @Override
    public OrcamentoValidadeResult executeValidade(Long id) {
        var orcamento = orcamentoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Orcamento nao encontrado: " + id));
        return new OrcamentoValidadeResult(orcamento.getId(), orcamento.estaExpirado());
    }
}
