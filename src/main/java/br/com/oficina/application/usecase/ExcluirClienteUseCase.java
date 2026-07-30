package br.com.oficina.application.usecase;

import br.com.oficina.application.command.ExcluirClienteCommand;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.ExcluirClienteInputPort;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExcluirClienteUseCase implements ExcluirClienteInputPort {

    private final ClienteRepositoryPort clienteRepositoryPort;

    @Override
    public void execute(ExcluirClienteCommand command) {
        if (clienteRepositoryPort.buscarPorId(command.id()).isEmpty()) {
            throw new RecursoNaoEncontradoException("Cliente nao encontrado: " + command.id());
        }
        if (clienteRepositoryPort.existeVeiculoVinculado(command.id())) {
            throw new NegocioException("Nao e possivel excluir cliente com veiculos vinculados");
        }
        if (clienteRepositoryPort.existeOrdemDeServicoVinculada(command.id())) {
            throw new NegocioException("Nao e possivel excluir cliente com ordens de servico vinculadas");
        }
        clienteRepositoryPort.excluir(command.id());
    }
}
