package br.com.oficina.adapters.out.persistence;

import br.com.oficina.adapters.out.persistence.mapper.OrdemDeServicoPersistenceMapper;
import br.com.oficina.adapters.out.persistence.repository.SpringDataOrdemDeServicoRepository;
import br.com.oficina.application.port.out.OrdemDeServicoRepositoryPort;
import br.com.oficina.domain.model.OrdemDeServico;
import br.com.oficina.domain.valueobject.StatusOS;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrdemDeServicoPersistenceAdapter implements OrdemDeServicoRepositoryPort {

    private final SpringDataOrdemDeServicoRepository repository;
    private final OrdemDeServicoPersistenceMapper mapper;

    @Override
    public OrdemDeServico salvar(OrdemDeServico ordemDeServico) {
        return mapper.toDomain(repository.save(mapper.toEntity(ordemDeServico)));
    }

    @Override
    public Optional<OrdemDeServico> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<OrdemDeServico> buscarPorNumero(String numero) {
        return repository.findByNumero(numero).map(mapper::toDomain);
    }

    @Override
    public List<OrdemDeServico> listarTodas() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<OrdemDeServico> listarFilaOperacional() {
        return repository.findFilaOperacional().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<OrdemDeServico> listarPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<OrdemDeServico> listarPorStatus(StatusOS status) {
        return repository.findByStatus(status).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<OrdemDeServico> listarPorVeiculo(Long veiculoId) {
        return repository.findByVeiculoId(veiculoId).stream().map(mapper::toDomain).toList();
    }
}
