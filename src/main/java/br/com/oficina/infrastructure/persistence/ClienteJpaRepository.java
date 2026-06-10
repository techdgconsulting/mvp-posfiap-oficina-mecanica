package br.com.oficina.infrastructure.persistence;

import br.com.oficina.domain.atendimento.cliente.Cliente;
import br.com.oficina.domain.atendimento.cliente.ClienteRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteJpaRepository extends JpaRepository<Cliente, Long>, ClienteRepository {

    @Override
    default Cliente salvar(Cliente cliente) {
        return save(cliente);
    }

    @Override
    default Optional<Cliente> buscarPorId(Long id) {
        return findById(id);
    }

    @Query("SELECT c FROM Cliente c WHERE c.documento.valor = :documento")
    Optional<Cliente> buscarPorDocumento(String documento);

    @Override
    default List<Cliente> listarTodos() {
        return findAll();
    }

    @Override
    default void excluir(Long id) {
        deleteById(id);
    }

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cliente c WHERE c.documento.valor = :documento")
    boolean existePorDocumento(String documento);
}
