package br.com.oficina.application.usecase;

import br.com.oficina.application.command.BaixarEstoqueCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.BaixarEstoqueInputPort;
import br.com.oficina.application.port.out.PecaRepositoryPort;
import br.com.oficina.application.query.PecaResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BaixarEstoqueUseCase implements BaixarEstoqueInputPort {

    private final PecaRepositoryPort pecaRepository;

    @Override
    public PecaResult execute(BaixarEstoqueCommand command) {
        var peca = pecaRepository.buscarPorId(command.pecaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Peca nao encontrada: " + command.pecaId()));
        peca.baixarEstoque(command.quantidade());
        return PecaResultMapper.toResult(pecaRepository.salvar(peca));
    }
}
