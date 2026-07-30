package br.com.oficina.application.port.out;

import br.com.oficina.domain.model.Usuario;
import java.util.Optional;

public interface UsuarioRepositoryPort {
    Usuario salvar(Usuario usuario);
    Optional<Usuario> buscarPorUsername(String username);
    boolean existePorUsername(String username);
}
