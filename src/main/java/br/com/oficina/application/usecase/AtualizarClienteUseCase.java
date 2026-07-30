package br.com.oficina.application.usecase;

import br.com.oficina.application.command.AtualizarClienteCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.AtualizarClienteInputPort;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.query.ClienteResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AtualizarClienteUseCase implements AtualizarClienteInputPort {

    private final ClienteRepositoryPort clienteRepositoryPort;

    @Override
    public ClienteResult execute(AtualizarClienteCommand command) {
        var cliente = clienteRepositoryPort.buscarPorId(command.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado: " + command.id()));
        cliente.atualizar(command.nome(), command.telefone(), command.email());
        return ClienteResultMapper.toResult(clienteRepositoryPort.salvar(cliente));
    }
}
