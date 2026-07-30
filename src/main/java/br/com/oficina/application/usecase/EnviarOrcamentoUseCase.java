package br.com.oficina.application.usecase;

import br.com.oficina.application.command.EnviarOrcamentoCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.EnviarOrcamentoInputPort;
import br.com.oficina.application.port.out.OrcamentoRepositoryPort;
import br.com.oficina.application.query.OrcamentoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnviarOrcamentoUseCase implements EnviarOrcamentoInputPort {

    private final OrcamentoRepositoryPort orcamentoRepository;

    @Override
    public OrcamentoResult execute(EnviarOrcamentoCommand command) {
        var orcamento = orcamentoRepository.buscarPorId(command.orcamentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Orcamento nao encontrado: " + command.orcamentoId()));
        orcamento.enviar();
        return OrcamentoResultMapper.toResult(orcamentoRepository.salvar(orcamento));
    }
}
