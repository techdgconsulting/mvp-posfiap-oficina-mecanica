package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.BuscarUsuarioPorLoginInputPort;
import br.com.oficina.application.port.out.UsuarioRepositoryPort;
import br.com.oficina.application.query.UsuarioResult;

public class BuscarUsuarioPorLoginUseCase implements BuscarUsuarioPorLoginInputPort {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public BuscarUsuarioPorLoginUseCase(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    public UsuarioResult execute(String username) {
        return usuarioRepositoryPort.buscarPorUsername(username)
                .map(UsuarioResultMapper::toResult)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado: " + username));
    }
}
