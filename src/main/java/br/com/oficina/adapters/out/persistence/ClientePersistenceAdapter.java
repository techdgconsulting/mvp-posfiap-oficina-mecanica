package br.com.oficina.adapters.out.persistence;

import br.com.oficina.adapters.out.persistence.mapper.ClientePersistenceMapper;
import br.com.oficina.adapters.out.persistence.repository.SpringDataClienteRepository;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.domain.model.Cliente;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientePersistenceAdapter implements ClienteRepositoryPort {

    private final SpringDataClienteRepository repository;
    private final ClientePersistenceMapper mapper;

    @Override
    public Cliente salvar(Cliente cliente) {
        return mapper.toDomain(repository.save(mapper.toEntity(cliente)));
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorDocumento(String documento) {
        return repository.findByDocumento(documento).map(mapper::toDomain);
    }

    @Override
    public List<Cliente> listarTodos() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void excluir(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existePorDocumento(String documento) {
        return repository.existsByDocumento(documento);
    }

    @Override
    public boolean existeVeiculoVinculado(Long clienteId) {
        return repository.existsVeiculoByClienteId(clienteId);
    }

    @Override
    public boolean existeOrdemDeServicoVinculada(Long clienteId) {
        return repository.existsOrdemDeServicoByClienteId(clienteId);
    }
}
