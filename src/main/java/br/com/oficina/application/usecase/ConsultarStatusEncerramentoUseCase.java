package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.ConsultarStatusEncerramentoInputPort;
import br.com.oficina.application.port.out.EncerramentoRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConsultarStatusEncerramentoUseCase implements ConsultarStatusEncerramentoInputPort {

    private final EncerramentoRepositoryPort encerramentoRepository;

    @Override
    public String executeStatus(Long id) {
        return encerramentoRepository.buscarPorId(id)
                .map(encerramento -> encerramento.getStatus().name())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Encerramento nao encontrado: " + id));
    }
}
