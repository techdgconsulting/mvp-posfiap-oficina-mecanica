package br.com.oficina.application.usecase;

import br.com.oficina.application.command.AtualizarPecaCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.AtualizarPecaInputPort;
import br.com.oficina.application.port.out.PecaRepositoryPort;
import br.com.oficina.application.query.PecaResult;
import br.com.oficina.domain.valueobject.Quantidade;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AtualizarPecaUseCase implements AtualizarPecaInputPort {

    private final PecaRepositoryPort pecaRepository;

    @Override
    public PecaResult execute(AtualizarPecaCommand command) {
        var peca = pecaRepository.buscarPorId(command.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Peca nao encontrada: " + command.id()));
        peca.atualizar(command.nome(), command.descricao(), command.valorUnitario(), command.estoqueMinimo());
        if (command.quantidadeEstoque() != null) {
            peca.alterarQuantidadeEstoque(new Quantidade(command.quantidadeEstoque()));
        }
        return PecaResultMapper.toResult(pecaRepository.salvar(peca));
    }
}
