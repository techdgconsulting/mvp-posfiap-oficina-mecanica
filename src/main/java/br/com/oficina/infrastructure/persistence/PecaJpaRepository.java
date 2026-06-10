package br.com.oficina.infrastructure.persistence;

import br.com.oficina.domain.estoque.Peca;
import br.com.oficina.domain.estoque.PecaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PecaJpaRepository extends JpaRepository<Peca, Long>, PecaRepository {

    @Override
    default Peca salvar(Peca peca) {
        return save(peca);
    }

    @Override
    default Optional<Peca> buscarPorId(Long id) {
        return findById(id);
    }

    @Override
    default List<Peca> listarTodas() {
        return findAll();
    }

    @Query("SELECT p FROM Peca p WHERE p.quantidadeEstoque.valor <= p.estoqueMinimo")
    List<Peca> listarComEstoqueBaixo();

    @Override
    default void excluir(Long id) {
        deleteById(id);
    }
}
