package br.com.oficina.adapters.out.persistence.repository;

import br.com.oficina.adapters.out.persistence.jpa.ExecucaoJpaEntity;
import br.com.oficina.domain.valueobject.StatusExecucao;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataExecucaoRepository extends JpaRepository<ExecucaoJpaEntity, Long> {

    Optional<ExecucaoJpaEntity> findByOrdemDeServicoId(Long ordemDeServicoId);

    List<ExecucaoJpaEntity> findByStatus(StatusExecucao status);
}
