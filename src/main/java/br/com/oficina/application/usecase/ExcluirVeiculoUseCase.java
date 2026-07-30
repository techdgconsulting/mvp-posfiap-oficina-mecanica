package br.com.oficina.application.usecase;

import br.com.oficina.application.command.ExcluirVeiculoCommand;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.ExcluirVeiculoInputPort;
import br.com.oficina.application.port.out.VeiculoRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExcluirVeiculoUseCase implements ExcluirVeiculoInputPort {

    private final VeiculoRepositoryPort veiculoRepositoryPort;

    @Override
    public void execute(ExcluirVeiculoCommand command) {
        if (veiculoRepositoryPort.buscarPorId(command.id()).isEmpty()) {
            throw new RecursoNaoEncontradoException("Veiculo nao encontrado: " + command.id());
        }
        if (veiculoRepositoryPort.existeOrdemDeServicoVinculada(command.id())) {
            throw new NegocioException("Nao e possivel excluir veiculo com Ordem de Servico vinculada.");
        }
        veiculoRepositoryPort.excluir(command.id());
    }
}
