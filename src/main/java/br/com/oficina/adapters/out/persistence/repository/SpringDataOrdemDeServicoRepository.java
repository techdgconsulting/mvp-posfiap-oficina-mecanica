package br.com.oficina.adapters.out.persistence.repository;

import br.com.oficina.adapters.out.persistence.jpa.OrdemDeServicoJpaEntity;
import br.com.oficina.domain.valueobject.StatusOS;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpringDataOrdemDeServicoRepository extends JpaRepository<OrdemDeServicoJpaEntity, Long> {
    Optional<OrdemDeServicoJpaEntity> findByNumero(String numero);
    @Query(value = """
            SELECT *
              FROM ordens_servico
             WHERE status IN ('EM_EXECUCAO', 'AGUARDANDO_APROVACAO', 'EM_DIAGNOSTICO', 'RECEBIDA')
             ORDER BY CASE status
                    WHEN 'EM_EXECUCAO' THEN 1
                    WHEN 'AGUARDANDO_APROVACAO' THEN 2
                    WHEN 'EM_DIAGNOSTICO' THEN 3
                    WHEN 'RECEBIDA' THEN 4
                    ELSE 5
                  END,
                  data_criacao ASC
            """, nativeQuery = true)
    List<OrdemDeServicoJpaEntity> findFilaOperacional();
    List<OrdemDeServicoJpaEntity> findByClienteId(Long clienteId);
    List<OrdemDeServicoJpaEntity> findByStatus(StatusOS status);
    List<OrdemDeServicoJpaEntity> findByVeiculoId(Long veiculoId);
}
