package br.com.oficina.adapters.out.persistence.repository;

import br.com.oficina.adapters.out.persistence.jpa.OrcamentoJpaEntity;
import br.com.oficina.domain.valueobject.StatusOrcamento;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOrcamentoRepository extends JpaRepository<OrcamentoJpaEntity, Long> {

    List<OrcamentoJpaEntity> findByOrdemDeServicoId(Long ordemDeServicoId);

    Optional<OrcamentoJpaEntity> findFirstByOrdemDeServicoIdAndStatusIn(
            Long ordemDeServicoId,
            Collection<StatusOrcamento> status);
}
