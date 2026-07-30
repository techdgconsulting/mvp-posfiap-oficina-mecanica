package br.com.oficina.adapters.out.persistence;

import br.com.oficina.adapters.out.persistence.mapper.PecaPersistenceMapper;
import br.com.oficina.adapters.out.persistence.repository.SpringDataPecaRepository;
import br.com.oficina.application.port.out.PecaRepositoryPort;
import br.com.oficina.domain.model.Peca;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PecaPersistenceAdapter implements PecaRepositoryPort {

    private final SpringDataPecaRepository repository;
    private final PecaPersistenceMapper mapper;

    @Override
    public Optional<Peca> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Peca salvar(Peca peca) {
        return mapper.toDomain(repository.save(mapper.toEntity(peca)));
    }

    @Override
    public List<Peca> listarTodas() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Peca> listarComEstoqueBaixo() {
        return repository.listarComEstoqueBaixo().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void excluir(Long id) {
        repository.deleteById(id);
    }
}
