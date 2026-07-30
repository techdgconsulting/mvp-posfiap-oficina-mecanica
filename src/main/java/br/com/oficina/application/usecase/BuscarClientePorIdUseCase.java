package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.BuscarClientePorIdInputPort;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.query.ClienteResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuscarClientePorIdUseCase implements BuscarClientePorIdInputPort {

    private final ClienteRepositoryPort clienteRepositoryPort;

    @Override
    public ClienteResult execute(Long id) {
        return clienteRepositoryPort.buscarPorId(id)
                .map(ClienteResultMapper::toResult)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado: " + id));
    }
}
