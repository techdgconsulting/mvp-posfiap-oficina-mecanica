package br.com.oficina.application.usecase;

import br.com.oficina.application.command.AtualizarVeiculoCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.AtualizarVeiculoInputPort;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.port.out.VeiculoRepositoryPort;
import br.com.oficina.application.query.VeiculoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AtualizarVeiculoUseCase implements AtualizarVeiculoInputPort {

    private final VeiculoRepositoryPort veiculoRepositoryPort;
    private final ClienteRepositoryPort clienteRepositoryPort;

    @Override
    public VeiculoResult execute(AtualizarVeiculoCommand command) {
        var veiculo = veiculoRepositoryPort.buscarPorId(command.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veiculo nao encontrado: " + command.id()));
        veiculo.atualizar(command.marca(), command.modelo(), command.ano());
        var salvo = veiculoRepositoryPort.salvar(veiculo);
        var cliente = clienteRepositoryPort.buscarPorId(salvo.getClienteId()).orElse(salvo.getCliente());
        return VeiculoResultMapper.toResult(salvo, cliente);
    }
}
