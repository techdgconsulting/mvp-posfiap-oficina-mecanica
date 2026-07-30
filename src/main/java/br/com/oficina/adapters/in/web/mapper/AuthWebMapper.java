package br.com.oficina.adapters.in.web.mapper;

import br.com.oficina.adapters.in.web.request.LoginRequest;
import br.com.oficina.adapters.in.web.request.RegistroRequest;
import br.com.oficina.adapters.in.web.response.LoginResponse;
import br.com.oficina.adapters.in.web.response.UsuarioResponse;
import br.com.oficina.application.command.LoginCommand;
import br.com.oficina.application.command.RegistrarUsuarioCommand;
import br.com.oficina.application.query.LoginResult;
import br.com.oficina.application.query.UsuarioResult;
import org.springframework.stereotype.Component;

@Component
public class AuthWebMapper {

    public LoginCommand toCommand(LoginRequest request) {
        return new LoginCommand(request.username(), request.password());
    }

    public RegistrarUsuarioCommand toCommand(RegistroRequest request) {
        return new RegistrarUsuarioCommand(request.username(), request.password(), request.role());
    }

    public LoginResponse toResponse(LoginResult result) {
        return new LoginResponse(result.token(), result.username(), result.role());
    }

    public UsuarioResponse toResponse(UsuarioResult result) {
        return new UsuarioResponse("Usuario criado com sucesso", result.username());
    }
}
