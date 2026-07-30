package br.com.oficina.application.port.in;

import br.com.oficina.application.command.RegistrarUsuarioCommand;
import br.com.oficina.application.query.UsuarioResult;

public interface RegistrarUsuarioInputPort {
    UsuarioResult execute(RegistrarUsuarioCommand command);
}
