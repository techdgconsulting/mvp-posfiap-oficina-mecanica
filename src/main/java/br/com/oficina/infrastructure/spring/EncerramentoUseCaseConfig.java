package br.com.oficina.infrastructure.spring;

import br.com.oficina.application.port.in.BuscarEncerramentoPorIdInputPort;
import br.com.oficina.application.port.in.BuscarEncerramentoPorOrdemServicoInputPort;
import br.com.oficina.application.port.in.ConsultarStatusEncerramentoInputPort;
import br.com.oficina.application.port.in.CriarEncerramentoInputPort;
import br.com.oficina.application.port.in.EncerrarOrdemServicoInputPort;
import br.com.oficina.application.port.out.EncerramentoRepositoryPort;
import br.com.oficina.application.usecase.BuscarEncerramentoPorIdUseCase;
import br.com.oficina.application.usecase.BuscarEncerramentoPorOrdemServicoUseCase;
import br.com.oficina.application.usecase.ConsultarStatusEncerramentoUseCase;
import br.com.oficina.application.usecase.CriarEncerramentoUseCase;
import br.com.oficina.application.usecase.EncerrarOrdemServicoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncerramentoUseCaseConfig {

    @Bean
    CriarEncerramentoInputPort criarEncerramentoInputPort(EncerramentoRepositoryPort encerramentoRepositoryPort) {
        return new CriarEncerramentoUseCase(encerramentoRepositoryPort);
    }

    @Bean
    EncerrarOrdemServicoInputPort encerrarOrdemServicoInputPort(
            EncerramentoRepositoryPort encerramentoRepositoryPort) {
        return new EncerrarOrdemServicoUseCase(encerramentoRepositoryPort);
    }

    @Bean
    BuscarEncerramentoPorIdInputPort buscarEncerramentoPorIdInputPort(
            EncerramentoRepositoryPort encerramentoRepositoryPort) {
        return new BuscarEncerramentoPorIdUseCase(encerramentoRepositoryPort);
    }

    @Bean
    BuscarEncerramentoPorOrdemServicoInputPort buscarEncerramentoPorOrdemServicoInputPort(
            EncerramentoRepositoryPort encerramentoRepositoryPort) {
        return new BuscarEncerramentoPorOrdemServicoUseCase(encerramentoRepositoryPort);
    }

    @Bean
    ConsultarStatusEncerramentoInputPort consultarStatusEncerramentoInputPort(
            EncerramentoRepositoryPort encerramentoRepositoryPort) {
        return new ConsultarStatusEncerramentoUseCase(encerramentoRepositoryPort);
    }
}
