package br.com.oficina.adapters.out.persistence.repository;

import br.com.oficina.adapters.out.persistence.jpa.VeiculoJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpringDataVeiculoRepository extends JpaRepository<VeiculoJpaEntity, Long> {

    Optional<VeiculoJpaEntity> findByPlaca(String placa);

    List<VeiculoJpaEntity> findByClienteId(Long clienteId);

    @Query(value = "SELECT COUNT(1) > 0 FROM ordens_servico WHERE veiculo_id = :veiculoId", nativeQuery = true)
    boolean existsOrdemDeServicoByVeiculoId(Long veiculoId);
}
