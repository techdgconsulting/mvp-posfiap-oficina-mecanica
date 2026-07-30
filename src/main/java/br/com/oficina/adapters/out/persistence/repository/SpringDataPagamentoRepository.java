package br.com.oficina.adapters.out.persistence.repository;

import br.com.oficina.adapters.out.persistence.jpa.PagamentoJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPagamentoRepository extends JpaRepository<PagamentoJpaEntity, Long> {

    Optional<PagamentoJpaEntity> findByOrdemDeServicoId(Long ordemDeServicoId);
}
