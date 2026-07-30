package br.com.oficina.application.usecase;

import br.com.oficina.application.command.LoginCommand;
import br.com.oficina.application.exception.CredenciaisInvalidasException;
import br.com.oficina.application.port.in.AutenticarLoginInputPort;
import br.com.oficina.application.port.out.PasswordHasherPort;
import br.com.oficina.application.port.out.TokenProviderPort;
import br.com.oficina.application.port.out.UsuarioRepositoryPort;
import br.com.oficina.application.query.LoginResult;

public class AutenticarLoginUseCase implements AutenticarLoginInputPort {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordHasherPort passwordHasherPort;
    private final TokenProviderPort tokenProviderPort;

    public AutenticarLoginUseCase(
            UsuarioRepositoryPort usuarioRepositoryPort,
            PasswordHasherPort passwordHasherPort,
            TokenProviderPort tokenProviderPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordHasherPort = passwordHasherPort;
        this.tokenProviderPort = tokenProviderPort;
    }

    @Override
    public LoginResult execute(LoginCommand command) {
        var usuario = usuarioRepositoryPort.buscarPorUsername(command.username())
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordHasherPort.matches(command.password(), usuario.getPassword())) {
            throw new CredenciaisInvalidasException();
        }

        var token = tokenProviderPort.gerarToken(usuario.getUsername(), usuario.getRole());
        return new LoginResult(token, usuario.getUsername(), usuario.getRole());
    }
}
