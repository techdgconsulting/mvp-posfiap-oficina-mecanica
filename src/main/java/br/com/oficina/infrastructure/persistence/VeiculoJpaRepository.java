package br.com.oficina.infrastructure.persistence;

import br.com.oficina.domain.atendimento.veiculo.Veiculo;
import br.com.oficina.domain.atendimento.veiculo.VeiculoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeiculoJpaRepository extends JpaRepository<Veiculo, Long>, VeiculoRepository {

    @Override
    default Veiculo salvar(Veiculo veiculo) {
        return save(veiculo);
    }

    @Override
    default Optional<Veiculo> buscarPorId(Long id) {
        return findById(id);
    }

    @Query("SELECT v FROM Veiculo v WHERE v.placa.valor = :placa")
    Optional<Veiculo> buscarPorPlaca(String placa);

    @Query("SELECT v FROM Veiculo v WHERE v.cliente.id = :clienteId")
    List<Veiculo> listarPorCliente(Long clienteId);

    @Override
    default List<Veiculo> listarTodos() {
        return findAll();
    }

    @Override
    default void excluir(Long id) {
        deleteById(id);
    }
}
