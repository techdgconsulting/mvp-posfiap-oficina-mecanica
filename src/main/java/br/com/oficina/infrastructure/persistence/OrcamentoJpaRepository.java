package br.com.oficina.infrastructure.persistence;

import br.com.oficina.domain.orcamento.Orcamento;
import br.com.oficina.domain.orcamento.OrcamentoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrcamentoJpaRepository extends JpaRepository<Orcamento, Long>, OrcamentoRepository {

    @Override
    default Orcamento salvar(Orcamento orcamento) {
        return save(orcamento);
    }

    @Override
    default Optional<Orcamento> buscarPorId(Long id) {
        return findById(id);
    }

    @Query("SELECT o FROM Orcamento o WHERE o.ordemDeServicoId = :ordemDeServicoId")
    List<Orcamento> listarPorOrdemDeServico(Long ordemDeServicoId);

    @Query("SELECT o FROM Orcamento o WHERE o.ordemDeServicoId = :ordemDeServicoId AND o.status IN ('PENDENTE','ENVIADO')")
    Optional<Orcamento> buscarAtivoByOrdemDeServico(Long ordemDeServicoId);
}
