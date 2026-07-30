package br.com.oficina.adapters.out.persistence;

import br.com.oficina.adapters.out.persistence.mapper.ExecucaoPersistenceMapper;
import br.com.oficina.adapters.out.persistence.repository.SpringDataExecucaoRepository;
import br.com.oficina.application.port.out.ExecucaoRepositoryPort;
import br.com.oficina.domain.model.Execucao;
import br.com.oficina.domain.valueobject.StatusExecucao;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExecucaoPersistenceAdapter implements ExecucaoRepositoryPort {

    private final SpringDataExecucaoRepository repository;
    private final ExecucaoPersistenceMapper mapper;

    @Override
    public Execucao salvar(Execucao execucao) {
        return mapper.toDomain(repository.save(mapper.toEntity(execucao)));
    }

    @Override
    public Optional<Execucao> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Execucao> buscarPorOrdemDeServico(Long ordemServicoId) {
        return repository.findByOrdemDeServicoId(ordemServicoId).map(mapper::toDomain);
    }

    @Override
    public List<Execucao> listarPorStatus(StatusExecucao status) {
        return repository.findByStatus(status).stream().map(mapper::toDomain).toList();
    }
}
