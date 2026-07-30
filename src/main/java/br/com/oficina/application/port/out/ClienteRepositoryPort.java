package br.com.oficina.application.port.out;

import br.com.oficina.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteRepositoryPort {
    Cliente salvar(Cliente cliente);
    Optional<Cliente> buscarPorId(Long id);
    Optional<Cliente> buscarPorDocumento(String documento);
    List<Cliente> listarTodos();
    void excluir(Long id);
    boolean existePorDocumento(String documento);
    boolean existeVeiculoVinculado(Long clienteId);
    boolean existeOrdemDeServicoVinculada(Long clienteId);
}
