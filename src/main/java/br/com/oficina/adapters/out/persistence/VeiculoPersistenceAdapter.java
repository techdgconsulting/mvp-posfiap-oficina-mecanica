package br.com.oficina.adapters.out.persistence;

import br.com.oficina.adapters.out.persistence.mapper.VeiculoPersistenceMapper;
import br.com.oficina.adapters.out.persistence.repository.SpringDataVeiculoRepository;
import br.com.oficina.application.port.out.VeiculoRepositoryPort;
import br.com.oficina.domain.model.Veiculo;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VeiculoPersistenceAdapter implements VeiculoRepositoryPort {

    private final SpringDataVeiculoRepository repository;
    private final VeiculoPersistenceMapper mapper;

    @Override
    public Veiculo salvar(Veiculo veiculo) {
        return mapper.toDomain(repository.save(mapper.toEntity(veiculo)));
    }

    @Override
    public Optional<Veiculo> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Veiculo> buscarPorPlaca(String placa) {
        return repository.findByPlaca(placa).map(mapper::toDomain);
    }

    @Override
    public List<Veiculo> listarPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Veiculo> listarTodos() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void excluir(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existeOrdemDeServicoVinculada(Long veiculoId) {
        return repository.existsOrdemDeServicoByVeiculoId(veiculoId);
    }
}
