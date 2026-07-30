package br.com.oficina.infrastructure.spring;

import br.com.oficina.application.port.in.AtualizarServicoInputPort;
import br.com.oficina.application.port.in.BuscarServicoPorIdInputPort;
import br.com.oficina.application.port.in.CriarServicoInputPort;
import br.com.oficina.application.port.in.ExcluirServicoInputPort;
import br.com.oficina.application.port.in.ListarServicosInputPort;
import br.com.oficina.application.port.out.ServicoRepositoryPort;
import br.com.oficina.application.usecase.AtualizarServicoUseCase;
import br.com.oficina.application.usecase.BuscarServicoPorIdUseCase;
import br.com.oficina.application.usecase.CriarServicoUseCase;
import br.com.oficina.application.usecase.ExcluirServicoUseCase;
import br.com.oficina.application.usecase.ListarServicosUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicoUseCaseConfig {

    @Bean
    CriarServicoInputPort criarServicoInputPort(ServicoRepositoryPort servicoRepositoryPort) {
        return new CriarServicoUseCase(servicoRepositoryPort);
    }

    @Bean
    AtualizarServicoInputPort atualizarServicoInputPort(ServicoRepositoryPort servicoRepositoryPort) {
        return new AtualizarServicoUseCase(servicoRepositoryPort);
    }

    @Bean
    BuscarServicoPorIdInputPort buscarServicoPorIdInputPort(ServicoRepositoryPort servicoRepositoryPort) {
        return new BuscarServicoPorIdUseCase(servicoRepositoryPort);
    }

    @Bean
    ListarServicosInputPort listarServicosInputPort(ServicoRepositoryPort servicoRepositoryPort) {
        return new ListarServicosUseCase(servicoRepositoryPort);
    }

    @Bean
    ExcluirServicoInputPort excluirServicoInputPort(ServicoRepositoryPort servicoRepositoryPort) {
        return new ExcluirServicoUseCase(servicoRepositoryPort);
    }
}
