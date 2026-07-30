package br.com.oficina.adapters.out.persistence;

import br.com.oficina.adapters.out.persistence.mapper.ServicoPersistenceMapper;
import br.com.oficina.adapters.out.persistence.repository.SpringDataServicoRepository;
import br.com.oficina.application.port.out.ServicoRepositoryPort;
import br.com.oficina.domain.model.Servico;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServicoPersistenceAdapter implements ServicoRepositoryPort {

    private final SpringDataServicoRepository repository;
    private final ServicoPersistenceMapper mapper;

    @Override
    public Servico salvar(Servico servico) {
        return mapper.toDomain(repository.save(mapper.toEntity(servico)));
    }

    @Override
    public Optional<Servico> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Servico> listarTodos() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void excluir(Long id) {
        repository.deleteById(id);
    }
}
