package br.com.oficina.application.usecase;

import br.com.oficina.application.query.UsuarioResult;
import br.com.oficina.domain.model.Usuario;

final class UsuarioResultMapper {

    private UsuarioResultMapper() {
    }

    static UsuarioResult toResult(Usuario usuario) {
        return new UsuarioResult(usuario.getId(), usuario.getUsername(), usuario.getRole());
    }
}
