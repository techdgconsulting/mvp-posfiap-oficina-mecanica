package br.com.oficina.domain.atendimento.cliente;

import java.util.Optional;

public interface ClienteRepository {
    Cliente salvar(Cliente cliente);
    Optional<Cliente> buscarPorId(Long id);
    Optional<Cliente> buscarPorDocumento(String documento);
    java.util.List<Cliente> listarTodos();
    void excluir(Long id);
    boolean existePorDocumento(String documento);
}
