package br.com.oficina.application.usecase;

import br.com.oficina.application.command.RejeitarOrcamentoDiretoCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.RejeitarOrcamentoDiretoInputPort;
import br.com.oficina.application.port.out.OrcamentoRepositoryPort;
import br.com.oficina.application.query.OrcamentoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RejeitarOrcamentoDiretoUseCase implements RejeitarOrcamentoDiretoInputPort {

    private final OrcamentoRepositoryPort orcamentoRepository;

    @Override
    public OrcamentoResult execute(RejeitarOrcamentoDiretoCommand command) {
        var orcamento = orcamentoRepository.buscarPorId(command.orcamentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Orcamento nao encontrado: " + command.orcamentoId()));
        orcamento.rejeitar();
        return OrcamentoResultMapper.toResult(orcamentoRepository.salvar(orcamento));
    }
}
