package br.com.oficina.application.usecase;

import br.com.oficina.application.command.ReporEstoqueCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.ReporEstoqueInputPort;
import br.com.oficina.application.port.out.PecaRepositoryPort;
import br.com.oficina.application.query.PecaResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReporEstoqueUseCase implements ReporEstoqueInputPort {

    private final PecaRepositoryPort pecaRepository;

    @Override
    public PecaResult execute(ReporEstoqueCommand command) {
        var peca = pecaRepository.buscarPorId(command.pecaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Peca nao encontrada: " + command.pecaId()));
        peca.reporEstoque(command.quantidade());
        return PecaResultMapper.toResult(pecaRepository.salvar(peca));
    }
}
