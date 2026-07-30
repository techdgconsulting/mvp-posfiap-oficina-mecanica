package br.com.oficina.adapters.out.persistence;

import br.com.oficina.adapters.out.persistence.mapper.UsuarioPersistenceMapper;
import br.com.oficina.adapters.out.persistence.repository.SpringDataUsuarioRepository;
import br.com.oficina.application.port.out.UsuarioRepositoryPort;
import br.com.oficina.domain.model.Usuario;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioPersistenceAdapter implements UsuarioRepositoryPort {

    private final SpringDataUsuarioRepository repository;
    private final UsuarioPersistenceMapper mapper;

    @Override
    public Usuario salvar(Usuario usuario) {
        return mapper.toDomain(repository.save(mapper.toEntity(usuario)));
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return repository.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public boolean existePorUsername(String username) {
        return repository.existsByUsername(username);
    }
}
