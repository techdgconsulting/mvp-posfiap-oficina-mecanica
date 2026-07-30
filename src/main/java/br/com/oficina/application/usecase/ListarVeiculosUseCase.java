package br.com.oficina.application.usecase;

import br.com.oficina.application.port.in.ListarVeiculosInputPort;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.port.out.VeiculoRepositoryPort;
import br.com.oficina.application.query.VeiculoResult;
import br.com.oficina.domain.model.Veiculo;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListarVeiculosUseCase implements ListarVeiculosInputPort {

    private final VeiculoRepositoryPort veiculoRepositoryPort;
    private final ClienteRepositoryPort clienteRepositoryPort;

    @Override
    public List<VeiculoResult> execute() {
        return veiculoRepositoryPort.listarTodos().stream()
                .map(this::toResult)
                .toList();
    }

    private VeiculoResult toResult(Veiculo veiculo) {
        var cliente = clienteRepositoryPort.buscarPorId(veiculo.getClienteId()).orElse(veiculo.getCliente());
        return VeiculoResultMapper.toResult(veiculo, cliente);
    }
}
