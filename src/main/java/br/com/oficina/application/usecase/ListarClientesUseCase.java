package br.com.oficina.application.usecase;

import br.com.oficina.application.port.in.ListarClientesInputPort;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.query.ClienteResult;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListarClientesUseCase implements ListarClientesInputPort {

    private final ClienteRepositoryPort clienteRepositoryPort;

    @Override
    public List<ClienteResult> execute() {
        return clienteRepositoryPort.listarTodos().stream()
                .map(ClienteResultMapper::toResult)
                .toList();
    }
}
