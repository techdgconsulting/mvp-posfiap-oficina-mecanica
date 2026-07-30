package br.com.oficina.adapters.out.persistence;

import br.com.oficina.adapters.out.persistence.mapper.EntregaPersistenceMapper;
import br.com.oficina.adapters.out.persistence.repository.SpringDataEntregaRepository;
import br.com.oficina.application.port.out.EntregaRepositoryPort;
import br.com.oficina.domain.model.Entrega;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntregaPersistenceAdapter implements EntregaRepositoryPort {

    private final SpringDataEntregaRepository repository;
    private final EntregaPersistenceMapper mapper;

    @Override
    public Entrega salvar(Entrega entrega) {
        return mapper.toDomain(repository.save(mapper.toEntity(entrega)));
    }

    @Override
    public Optional<Entrega> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Entrega> buscarPorOrdemDeServico(Long ordemServicoId) {
        return repository.findByOrdemDeServicoId(ordemServicoId).map(mapper::toDomain);
    }
}
