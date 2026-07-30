package br.com.oficina.application.usecase;

import br.com.oficina.application.command.VerificarDisponibilidadePecaCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.VerificarDisponibilidadePecaInputPort;
import br.com.oficina.application.port.out.PecaRepositoryPort;
import br.com.oficina.application.query.DisponibilidadePecaResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VerificarDisponibilidadePecaUseCase implements VerificarDisponibilidadePecaInputPort {

    private final PecaRepositoryPort pecaRepository;

    @Override
    public DisponibilidadePecaResult execute(VerificarDisponibilidadePecaCommand command) {
        var peca = pecaRepository.buscarPorId(command.pecaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Peca nao encontrada: " + command.pecaId()));
        return new DisponibilidadePecaResult(
                command.pecaId(),
                command.quantidade(),
                peca.getQuantidadeEstoqueValor(),
                peca.temDisponibilidade(command.quantidade()));
    }
}
