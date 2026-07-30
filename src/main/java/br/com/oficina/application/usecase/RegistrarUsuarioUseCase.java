package br.com.oficina.application.usecase;

import br.com.oficina.application.command.RegistrarUsuarioCommand;
import br.com.oficina.application.port.in.RegistrarUsuarioInputPort;
import br.com.oficina.application.port.out.PasswordHasherPort;
import br.com.oficina.application.port.out.UsuarioRepositoryPort;
import br.com.oficina.application.query.UsuarioResult;
import br.com.oficina.domain.model.Usuario;

public class RegistrarUsuarioUseCase implements RegistrarUsuarioInputPort {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordHasherPort passwordHasherPort;

    public RegistrarUsuarioUseCase(
            UsuarioRepositoryPort usuarioRepositoryPort,
            PasswordHasherPort passwordHasherPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordHasherPort = passwordHasherPort;
    }

    @Override
    public UsuarioResult execute(RegistrarUsuarioCommand command) {
        if (usuarioRepositoryPort.existePorUsername(command.username())) {
            throw new IllegalArgumentException("Username já existe");
        }

        var passwordHash = passwordHasherPort.hash(command.password());
        var usuario = Usuario.criar(command.username(), passwordHash, command.role());
        return UsuarioResultMapper.toResult(usuarioRepositoryPort.salvar(usuario));
    }
}
