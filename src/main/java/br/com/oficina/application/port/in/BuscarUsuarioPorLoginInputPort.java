package br.com.oficina.application.port.in;

import br.com.oficina.application.query.UsuarioResult;

public interface BuscarUsuarioPorLoginInputPort {
    UsuarioResult execute(String username);
}
