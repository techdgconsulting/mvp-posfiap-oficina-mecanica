package br.com.oficina.adapters.out.persistence.repository;

import br.com.oficina.adapters.out.persistence.jpa.EntregaJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataEntregaRepository extends JpaRepository<EntregaJpaEntity, Long> {

    Optional<EntregaJpaEntity> findByOrdemDeServicoId(Long ordemDeServicoId);
}
