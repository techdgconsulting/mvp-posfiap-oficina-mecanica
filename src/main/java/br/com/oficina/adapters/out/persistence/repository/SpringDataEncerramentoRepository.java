package br.com.oficina.adapters.out.persistence.repository;

import br.com.oficina.adapters.out.persistence.jpa.EncerramentoJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataEncerramentoRepository extends JpaRepository<EncerramentoJpaEntity, Long> {

    Optional<EncerramentoJpaEntity> findByOrdemDeServicoId(Long ordemDeServicoId);
}
