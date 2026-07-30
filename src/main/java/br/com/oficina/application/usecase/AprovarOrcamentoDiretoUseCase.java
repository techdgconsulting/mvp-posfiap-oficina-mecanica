package br.com.oficina.application.usecase;

import br.com.oficina.application.command.AprovarOrcamentoDiretoCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.AprovarOrcamentoDiretoInputPort;
import br.com.oficina.application.port.out.OrcamentoRepositoryPort;
import br.com.oficina.application.query.OrcamentoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AprovarOrcamentoDiretoUseCase implements AprovarOrcamentoDiretoInputPort {

    private final OrcamentoRepositoryPort orcamentoRepository;

    @Override
    public OrcamentoResult execute(AprovarOrcamentoDiretoCommand command) {
        var orcamento = orcamentoRepository.buscarPorId(command.orcamentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Orcamento nao encontrado: " + command.orcamentoId()));
        orcamento.aprovar();
        return OrcamentoResultMapper.toResult(orcamentoRepository.salvar(orcamento));
    }
}
