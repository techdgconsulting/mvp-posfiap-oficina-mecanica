package br.com.oficina.application.port.in;

import br.com.oficina.application.command.LoginCommand;
import br.com.oficina.application.query.LoginResult;

public interface AutenticarLoginInputPort {
    LoginResult execute(LoginCommand command);
}
