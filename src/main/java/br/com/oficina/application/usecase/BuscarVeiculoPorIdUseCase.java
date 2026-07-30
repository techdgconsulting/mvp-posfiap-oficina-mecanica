package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.BuscarVeiculoPorIdInputPort;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.port.out.VeiculoRepositoryPort;
import br.com.oficina.application.query.VeiculoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuscarVeiculoPorIdUseCase implements BuscarVeiculoPorIdInputPort {

    private final VeiculoRepositoryPort veiculoRepositoryPort;
    private final ClienteRepositoryPort clienteRepositoryPort;

    @Override
    public VeiculoResult execute(Long id) {
        var veiculo = veiculoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veiculo nao encontrado: " + id));
        var cliente = clienteRepositoryPort.buscarPorId(veiculo.getClienteId()).orElse(veiculo.getCliente());
        return VeiculoResultMapper.toResult(veiculo, cliente);
    }
}
