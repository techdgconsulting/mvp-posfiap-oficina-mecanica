package br.com.oficina.infrastructure.spring;

import br.com.oficina.application.port.in.AtualizarVeiculoInputPort;
import br.com.oficina.application.port.in.BuscarVeiculoPorIdInputPort;
import br.com.oficina.application.port.in.CriarVeiculoInputPort;
import br.com.oficina.application.port.in.ExcluirVeiculoInputPort;
import br.com.oficina.application.port.in.ListarVeiculosInputPort;
import br.com.oficina.application.port.in.ListarVeiculosPorClienteInputPort;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.port.out.VeiculoRepositoryPort;
import br.com.oficina.application.usecase.AtualizarVeiculoUseCase;
import br.com.oficina.application.usecase.BuscarVeiculoPorIdUseCase;
import br.com.oficina.application.usecase.CriarVeiculoUseCase;
import br.com.oficina.application.usecase.ExcluirVeiculoUseCase;
import br.com.oficina.application.usecase.ListarVeiculosPorClienteUseCase;
import br.com.oficina.application.usecase.ListarVeiculosUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VeiculoUseCaseConfig {

    @Bean
    CriarVeiculoInputPort criarVeiculoInputPort(
            VeiculoRepositoryPort veiculoRepositoryPort,
            ClienteRepositoryPort clienteRepositoryPort) {
        return new CriarVeiculoUseCase(veiculoRepositoryPort, clienteRepositoryPort);
    }

    @Bean
    AtualizarVeiculoInputPort atualizarVeiculoInputPort(
            VeiculoRepositoryPort veiculoRepositoryPort,
            ClienteRepositoryPort clienteRepositoryPort) {
        return new AtualizarVeiculoUseCase(veiculoRepositoryPort, clienteRepositoryPort);
    }

    @Bean
    BuscarVeiculoPorIdInputPort buscarVeiculoPorIdInputPort(
            VeiculoRepositoryPort veiculoRepositoryPort,
            ClienteRepositoryPort clienteRepositoryPort) {
        return new BuscarVeiculoPorIdUseCase(veiculoRepositoryPort, clienteRepositoryPort);
    }

    @Bean
    ListarVeiculosInputPort listarVeiculosInputPort(
            VeiculoRepositoryPort veiculoRepositoryPort,
            ClienteRepositoryPort clienteRepositoryPort) {
        return new ListarVeiculosUseCase(veiculoRepositoryPort, clienteRepositoryPort);
    }

    @Bean
    ListarVeiculosPorClienteInputPort listarVeiculosPorClienteInputPort(
            VeiculoRepositoryPort veiculoRepositoryPort,
            ClienteRepositoryPort clienteRepositoryPort) {
        return new ListarVeiculosPorClienteUseCase(veiculoRepositoryPort, clienteRepositoryPort);
    }

    @Bean
    ExcluirVeiculoInputPort excluirVeiculoInputPort(VeiculoRepositoryPort veiculoRepositoryPort) {
        return new ExcluirVeiculoUseCase(veiculoRepositoryPort);
    }
}
