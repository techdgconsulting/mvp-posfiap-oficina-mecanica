package br.com.oficina.infrastructure.persistence;

import br.com.oficina.domain.ordemservico.OrdemDeServico;
import br.com.oficina.domain.ordemservico.OrdemDeServicoRepository;
import br.com.oficina.domain.ordemservico.StatusOS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdemDeServicoJpaRepository extends JpaRepository<OrdemDeServico, Long>, OrdemDeServicoRepository {

    @Override
    default OrdemDeServico salvar(OrdemDeServico ordemDeServico) {
        return save(ordemDeServico);
    }

    @Override
    default Optional<OrdemDeServico> buscarPorId(Long id) {
        return findById(id);
    }

    @Override
    default Optional<OrdemDeServico> buscarPorNumero(String numero) {
        return findByNumero(numero);
    }

    Optional<OrdemDeServico> findByNumero(String numero);

    @Override
    default List<OrdemDeServico> listarTodas() {
        return findAll();
    }

    @Query("SELECT o FROM OrdemDeServico o WHERE o.cliente.id = :clienteId")
    List<OrdemDeServico> listarPorCliente(Long clienteId);

    @Query("SELECT o FROM OrdemDeServico o WHERE o.status = :status")
    List<OrdemDeServico> listarPorStatus(StatusOS status);

    @Query("SELECT o FROM OrdemDeServico o WHERE o.veiculo.id = :veiculoId")
    List<OrdemDeServico> listarPorVeiculo(Long veiculoId);
}
