package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.BuscarEntregaPorIdInputPort;
import br.com.oficina.application.port.out.EntregaRepositoryPort;
import br.com.oficina.application.query.EntregaResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuscarEntregaPorIdUseCase implements BuscarEntregaPorIdInputPort {

    private final EntregaRepositoryPort entregaRepository;

    @Override
    public EntregaResult execute(Long id) {
        return entregaRepository.buscarPorId(id)
                .map(EntregaResultMapper::toResult)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Entrega nao encontrada: " + id));
    }
}
