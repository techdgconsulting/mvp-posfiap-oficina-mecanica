package br.com.oficina.application.usecase;

import br.com.oficina.application.command.CriarVeiculoCommand;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.CriarVeiculoInputPort;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.port.out.VeiculoRepositoryPort;
import br.com.oficina.application.query.VeiculoResult;
import br.com.oficina.domain.model.Veiculo;
import br.com.oficina.domain.valueobject.Placa;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CriarVeiculoUseCase implements CriarVeiculoInputPort {

    private final VeiculoRepositoryPort veiculoRepositoryPort;
    private final ClienteRepositoryPort clienteRepositoryPort;

    @Override
    public VeiculoResult execute(CriarVeiculoCommand command) {
        var cliente = clienteRepositoryPort.buscarPorId(command.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado: " + command.clienteId()));

        var placa = new Placa(command.placa());
        if (veiculoRepositoryPort.buscarPorPlaca(placa.getValor()).isPresent()) {
            throw new NegocioException("Ja existe um veiculo cadastrado com a placa: " + placa.getValor());
        }

        var veiculo = new Veiculo(placa, command.marca(), command.modelo(), command.ano(), cliente);
        return VeiculoResultMapper.toResult(veiculoRepositoryPort.salvar(veiculo), cliente);
    }
}
