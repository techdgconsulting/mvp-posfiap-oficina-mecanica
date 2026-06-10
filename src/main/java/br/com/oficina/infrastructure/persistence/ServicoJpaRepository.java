package br.com.oficina.infrastructure.persistence;

import br.com.oficina.domain.servico.Servico;
import br.com.oficina.domain.servico.ServicoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicoJpaRepository extends JpaRepository<Servico, Long>, ServicoRepository {

    @Override
    default Servico salvar(Servico servico) {
        return save(servico);
    }

    @Override
    default Optional<Servico> buscarPorId(Long id) {
        return findById(id);
    }

    @Override
    default List<Servico> listarTodos() {
        return findAll();
    }

    @Override
    default void excluir(Long id) {
        deleteById(id);
    }
}
