package br.com.oficina.infrastructure.persistence;

import br.com.oficina.domain.encerramento.Encerramento;
import br.com.oficina.domain.encerramento.EncerramentoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EncerramentoJpaRepository extends JpaRepository<Encerramento, Long>, EncerramentoRepository {

    @Override
    default Encerramento salvar(Encerramento encerramento) {
        return save(encerramento);
    }

    @Override
    default Optional<Encerramento> buscarPorId(Long id) {
        return findById(id);
    }

    @Override
    @Query("SELECT e FROM Encerramento e WHERE e.ordemDeServicoId = :osId")
    Optional<Encerramento> buscarPorOrdemDeServico(Long osId);
}
