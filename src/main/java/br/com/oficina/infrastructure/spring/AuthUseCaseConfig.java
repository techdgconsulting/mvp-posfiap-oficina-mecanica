package br.com.oficina.infrastructure.spring;

import br.com.oficina.application.port.in.AutenticarLoginInputPort;
import br.com.oficina.application.port.in.BuscarUsuarioPorLoginInputPort;
import br.com.oficina.application.port.in.RegistrarUsuarioInputPort;
import br.com.oficina.application.port.out.PasswordHasherPort;
import br.com.oficina.application.port.out.TokenProviderPort;
import br.com.oficina.application.port.out.UsuarioRepositoryPort;
import br.com.oficina.application.usecase.AutenticarLoginUseCase;
import br.com.oficina.application.usecase.BuscarUsuarioPorLoginUseCase;
import br.com.oficina.application.usecase.RegistrarUsuarioUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthUseCaseConfig {

    @Bean
    AutenticarLoginInputPort autenticarLoginInputPort(
            UsuarioRepositoryPort usuarioRepositoryPort,
            PasswordHasherPort passwordHasherPort,
            TokenProviderPort tokenProviderPort) {
        return new AutenticarLoginUseCase(usuarioRepositoryPort, passwordHasherPort, tokenProviderPort);
    }

    @Bean
    RegistrarUsuarioInputPort registrarUsuarioInputPort(
            UsuarioRepositoryPort usuarioRepositoryPort,
            PasswordHasherPort passwordHasherPort) {
        return new RegistrarUsuarioUseCase(usuarioRepositoryPort, passwordHasherPort);
    }

    @Bean
    BuscarUsuarioPorLoginInputPort buscarUsuarioPorLoginInputPort(UsuarioRepositoryPort usuarioRepositoryPort) {
        return new BuscarUsuarioPorLoginUseCase(usuarioRepositoryPort);
    }
}
