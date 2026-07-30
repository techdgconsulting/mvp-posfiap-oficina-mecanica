package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.BuscarPecaPorIdInputPort;
import br.com.oficina.application.port.out.PecaRepositoryPort;
import br.com.oficina.application.query.PecaResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuscarPecaPorIdUseCase implements BuscarPecaPorIdInputPort {

    private final PecaRepositoryPort pecaRepository;

    @Override
    public PecaResult execute(Long id) {
        return pecaRepository.buscarPorId(id)
                .map(PecaResultMapper::toResult)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Peca nao encontrada: " + id));
    }
}
