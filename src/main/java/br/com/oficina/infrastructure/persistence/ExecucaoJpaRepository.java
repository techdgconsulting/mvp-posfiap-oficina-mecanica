package br.com.oficina.infrastructure.persistence;

import br.com.oficina.domain.execucao.Execucao;
import br.com.oficina.domain.execucao.ExecucaoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExecucaoJpaRepository extends JpaRepository<Execucao, Long>, ExecucaoRepository {

    @Override
    default Execucao salvar(Execucao execucao) {
        return save(execucao);
    }

    @Override
    default Optional<Execucao> buscarPorId(Long id) {
        return findById(id);
    }

    @Query("SELECT e FROM Execucao e WHERE e.ordemDeServicoId = :ordemDeServicoId")
    Optional<Execucao> buscarPorOrdemDeServico(Long ordemDeServicoId);
}
