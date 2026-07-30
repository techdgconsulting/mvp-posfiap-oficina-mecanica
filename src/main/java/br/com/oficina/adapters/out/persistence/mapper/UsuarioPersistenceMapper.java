package br.com.oficina.adapters.out.persistence.mapper;

import br.com.oficina.adapters.out.persistence.jpa.UsuarioJpaEntity;
import br.com.oficina.domain.model.Usuario;
import br.com.oficina.domain.valueobject.PerfilUsuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioPersistenceMapper {

    public UsuarioJpaEntity toEntity(Usuario usuario) {
        return new UsuarioJpaEntity(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getRole());
    }

    public Usuario toDomain(UsuarioJpaEntity entity) {
        return new Usuario(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                PerfilUsuario.from(entity.getRole()));
    }
}
