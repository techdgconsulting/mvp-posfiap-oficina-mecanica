package br.com.oficina.application.port.out;

import br.com.oficina.domain.model.Veiculo;
import java.util.List;
import java.util.Optional;

public interface VeiculoRepositoryPort {
    Veiculo salvar(Veiculo veiculo);
    Optional<Veiculo> buscarPorId(Long id);
    Optional<Veiculo> buscarPorPlaca(String placa);
    List<Veiculo> listarPorCliente(Long clienteId);
    List<Veiculo> listarTodos();
    void excluir(Long id);
    boolean existeOrdemDeServicoVinculada(Long veiculoId);
}
