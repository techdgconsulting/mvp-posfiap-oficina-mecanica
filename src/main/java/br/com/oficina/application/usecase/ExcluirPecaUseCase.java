package br.com.oficina.application.usecase;

import br.com.oficina.application.command.ExcluirPecaCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.ExcluirPecaInputPort;
import br.com.oficina.application.port.out.PecaRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExcluirPecaUseCase implements ExcluirPecaInputPort {

    private final PecaRepositoryPort pecaRepository;

    @Override
    public void execute(ExcluirPecaCommand command) {
        pecaRepository.buscarPorId(command.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Peca nao encontrada: " + command.id()));
        pecaRepository.excluir(command.id());
    }
}
