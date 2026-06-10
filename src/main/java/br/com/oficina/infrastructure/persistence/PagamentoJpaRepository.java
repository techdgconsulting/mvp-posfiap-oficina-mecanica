package br.com.oficina.infrastructure.persistence;

import br.com.oficina.domain.financeiro.Pagamento;
import br.com.oficina.domain.financeiro.PagamentoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PagamentoJpaRepository extends JpaRepository<Pagamento, Long>, PagamentoRepository {

    @Override
    default Pagamento salvar(Pagamento pagamento) {
        return save(pagamento);
    }

    @Override
    default Optional<Pagamento> buscarPorId(Long id) {
        return findById(id);
    }

    @Query("SELECT p FROM Pagamento p WHERE p.ordemDeServicoId = :ordemDeServicoId")
    Optional<Pagamento> buscarPorOrdemDeServico(Long ordemDeServicoId);
}
