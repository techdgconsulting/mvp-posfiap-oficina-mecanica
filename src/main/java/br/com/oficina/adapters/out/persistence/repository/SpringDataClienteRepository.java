package br.com.oficina.adapters.out.persistence.repository;

import br.com.oficina.adapters.out.persistence.jpa.ClienteJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpringDataClienteRepository extends JpaRepository<ClienteJpaEntity, Long> {

    Optional<ClienteJpaEntity> findByDocumento(String documento);

    boolean existsByDocumento(String documento);

    @Query(value = "SELECT COUNT(1) > 0 FROM veiculos WHERE cliente_id = :clienteId", nativeQuery = true)
    boolean existsVeiculoByClienteId(Long clienteId);

    @Query(value = "SELECT COUNT(1) > 0 FROM ordens_servico WHERE cliente_id = :clienteId", nativeQuery = true)
    boolean existsOrdemDeServicoByClienteId(Long clienteId);
}
