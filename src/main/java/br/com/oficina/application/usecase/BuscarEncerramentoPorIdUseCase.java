package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.BuscarEncerramentoPorIdInputPort;
import br.com.oficina.application.port.out.EncerramentoRepositoryPort;
import br.com.oficina.application.query.EncerramentoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuscarEncerramentoPorIdUseCase implements BuscarEncerramentoPorIdInputPort {

    private final EncerramentoRepositoryPort encerramentoRepository;

    @Override
    public EncerramentoResult execute(Long id) {
        return encerramentoRepository.buscarPorId(id)
                .map(EncerramentoResultMapper::toResult)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Encerramento nao encontrado: " + id));
    }
}
