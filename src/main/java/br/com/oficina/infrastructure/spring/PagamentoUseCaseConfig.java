package br.com.oficina.infrastructure.spring;

import br.com.oficina.application.port.in.AprovarPagamentoInputPort;
import br.com.oficina.application.port.in.BuscarPagamentoPorIdInputPort;
import br.com.oficina.application.port.in.BuscarPagamentoPorOrdemServicoInputPort;
import br.com.oficina.application.port.in.ConsultarStatusPagamentoInputPort;
import br.com.oficina.application.port.in.CriarPagamentoInputPort;
import br.com.oficina.application.port.in.ProcessarPagamentoInputPort;
import br.com.oficina.application.port.in.RecusarPagamentoInputPort;
import br.com.oficina.application.port.out.PagamentoGatewayPort;
import br.com.oficina.application.port.out.PagamentoRepositoryPort;
import br.com.oficina.application.usecase.AprovarPagamentoUseCase;
import br.com.oficina.application.usecase.BuscarPagamentoPorIdUseCase;
import br.com.oficina.application.usecase.BuscarPagamentoPorOrdemServicoUseCase;
import br.com.oficina.application.usecase.ConsultarStatusPagamentoUseCase;
import br.com.oficina.application.usecase.CriarPagamentoUseCase;
import br.com.oficina.application.usecase.ProcessarPagamentoUseCase;
import br.com.oficina.application.usecase.RecusarPagamentoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PagamentoUseCaseConfig {

    @Bean
    CriarPagamentoInputPort criarPagamentoInputPort(PagamentoRepositoryPort pagamentoRepositoryPort) {
        return new CriarPagamentoUseCase(pagamentoRepositoryPort);
    }

    @Bean
    BuscarPagamentoPorIdInputPort buscarPagamentoPorIdInputPort(PagamentoRepositoryPort pagamentoRepositoryPort) {
        return new BuscarPagamentoPorIdUseCase(pagamentoRepositoryPort);
    }

    @Bean
    BuscarPagamentoPorOrdemServicoInputPort buscarPagamentoPorOrdemServicoInputPort(
            PagamentoRepositoryPort pagamentoRepositoryPort) {
        return new BuscarPagamentoPorOrdemServicoUseCase(pagamentoRepositoryPort);
    }

    @Bean
    ProcessarPagamentoInputPort processarPagamentoInputPort(
            PagamentoRepositoryPort pagamentoRepositoryPort,
            PagamentoGatewayPort pagamentoGatewayPort) {
        return new ProcessarPagamentoUseCase(pagamentoRepositoryPort, pagamentoGatewayPort);
    }

    @Bean
    AprovarPagamentoInputPort aprovarPagamentoInputPort(PagamentoRepositoryPort pagamentoRepositoryPort) {
        return new AprovarPagamentoUseCase(pagamentoRepositoryPort);
    }

    @Bean
    RecusarPagamentoInputPort recusarPagamentoInputPort(PagamentoRepositoryPort pagamentoRepositoryPort) {
        return new RecusarPagamentoUseCase(pagamentoRepositoryPort);
    }

    @Bean
    ConsultarStatusPagamentoInputPort consultarStatusPagamentoInputPort(PagamentoRepositoryPort pagamentoRepositoryPort) {
        return new ConsultarStatusPagamentoUseCase(pagamentoRepositoryPort);
    }
}
