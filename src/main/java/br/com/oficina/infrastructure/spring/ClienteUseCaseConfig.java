package br.com.oficina.infrastructure.spring;

import br.com.oficina.application.port.in.AtualizarClienteInputPort;
import br.com.oficina.application.port.in.BuscarClientePorDocumentoInputPort;
import br.com.oficina.application.port.in.BuscarClientePorIdInputPort;
import br.com.oficina.application.port.in.CriarClienteInputPort;
import br.com.oficina.application.port.in.ExcluirClienteInputPort;
import br.com.oficina.application.port.in.ListarClientesInputPort;
import br.com.oficina.application.port.out.BuscarEnderecoPorCepPort;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.usecase.AtualizarClienteUseCase;
import br.com.oficina.application.usecase.BuscarClientePorDocumentoUseCase;
import br.com.oficina.application.usecase.BuscarClientePorIdUseCase;
import br.com.oficina.application.usecase.CriarClienteUseCase;
import br.com.oficina.application.usecase.ExcluirClienteUseCase;
import br.com.oficina.application.usecase.ListarClientesUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClienteUseCaseConfig {

    @Bean
    CriarClienteInputPort criarClienteInputPort(
            ClienteRepositoryPort clienteRepositoryPort,
            BuscarEnderecoPorCepPort buscarEnderecoPorCepPort) {
        return new CriarClienteUseCase(clienteRepositoryPort, buscarEnderecoPorCepPort);
    }

    @Bean
    AtualizarClienteInputPort atualizarClienteInputPort(ClienteRepositoryPort clienteRepositoryPort) {
        return new AtualizarClienteUseCase(clienteRepositoryPort);
    }

    @Bean
    ExcluirClienteInputPort excluirClienteInputPort(ClienteRepositoryPort clienteRepositoryPort) {
        return new ExcluirClienteUseCase(clienteRepositoryPort);
    }

    @Bean
    BuscarClientePorIdInputPort buscarClientePorIdInputPort(ClienteRepositoryPort clienteRepositoryPort) {
        return new BuscarClientePorIdUseCase(clienteRepositoryPort);
    }

    @Bean
    BuscarClientePorDocumentoInputPort buscarClientePorDocumentoInputPort(ClienteRepositoryPort clienteRepositoryPort) {
        return new BuscarClientePorDocumentoUseCase(clienteRepositoryPort);
    }

    @Bean
    ListarClientesInputPort listarClientesInputPort(ClienteRepositoryPort clienteRepositoryPort) {
        return new ListarClientesUseCase(clienteRepositoryPort);
    }
}
