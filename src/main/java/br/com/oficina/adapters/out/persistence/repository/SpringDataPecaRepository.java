package br.com.oficina.adapters.out.persistence.repository;

import br.com.oficina.adapters.out.persistence.jpa.PecaJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpringDataPecaRepository extends JpaRepository<PecaJpaEntity, Long> {

    @Query("SELECT p FROM PecaJpaEntity p WHERE p.quantidadeEstoque <= p.estoqueMinimo")
    List<PecaJpaEntity> listarComEstoqueBaixo();
}
