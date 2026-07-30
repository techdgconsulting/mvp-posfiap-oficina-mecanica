package br.com.oficina.adapters.out.persistence;

import br.com.oficina.adapters.out.persistence.mapper.EncerramentoPersistenceMapper;
import br.com.oficina.adapters.out.persistence.repository.SpringDataEncerramentoRepository;
import br.com.oficina.application.port.out.EncerramentoRepositoryPort;
import br.com.oficina.domain.model.Encerramento;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EncerramentoPersistenceAdapter implements EncerramentoRepositoryPort {

    private final SpringDataEncerramentoRepository repository;
    private final EncerramentoPersistenceMapper mapper;

    @Override
    public Encerramento salvar(Encerramento encerramento) {
        return mapper.toDomain(repository.save(mapper.toEntity(encerramento)));
    }

    @Override
    public Optional<Encerramento> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Encerramento> buscarPorOrdemDeServico(Long ordemServicoId) {
        return repository.findByOrdemDeServicoId(ordemServicoId).map(mapper::toDomain);
    }
}
