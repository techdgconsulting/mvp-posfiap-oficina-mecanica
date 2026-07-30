package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.ConsultarStatusEntregaInputPort;
import br.com.oficina.application.port.out.EntregaRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConsultarStatusEntregaUseCase implements ConsultarStatusEntregaInputPort {

    private final EntregaRepositoryPort entregaRepository;

    @Override
    public String executeStatus(Long id) {
        return entregaRepository.buscarPorId(id)
                .map(entrega -> entrega.getStatus().name())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Entrega nao encontrada: " + id));
    }
}
