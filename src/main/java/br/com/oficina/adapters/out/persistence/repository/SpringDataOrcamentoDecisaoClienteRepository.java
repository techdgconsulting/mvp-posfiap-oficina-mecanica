package br.com.oficina.adapters.out.persistence.repository;

import br.com.oficina.adapters.out.persistence.jpa.OrcamentoDecisaoClienteJpaEntity;
import br.com.oficina.domain.valueobject.StatusDecisaoCliente;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOrcamentoDecisaoClienteRepository
        extends JpaRepository<OrcamentoDecisaoClienteJpaEntity, Long> {

    Optional<OrcamentoDecisaoClienteJpaEntity> findByTokenHash(String tokenHash);

    List<OrcamentoDecisaoClienteJpaEntity> findByOrcamentoIdAndStatus(
            Long orcamentoId,
            StatusDecisaoCliente status);
}
