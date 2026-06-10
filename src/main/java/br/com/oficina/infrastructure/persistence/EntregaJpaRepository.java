package br.com.oficina.infrastructure.persistence;

import br.com.oficina.domain.entrega.Entrega;
import br.com.oficina.domain.entrega.EntregaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntregaJpaRepository extends JpaRepository<Entrega, Long>, EntregaRepository {

    @Override
    default Entrega salvar(Entrega entrega) {
        return save(entrega);
    }

    @Override
    default Optional<Entrega> buscarPorId(Long id) {
        return findById(id);
    }

    @Override
    @Query("SELECT e FROM Entrega e WHERE e.ordemDeServicoId = :osId")
    Optional<Entrega> buscarPorOrdemDeServico(Long osId);
}
