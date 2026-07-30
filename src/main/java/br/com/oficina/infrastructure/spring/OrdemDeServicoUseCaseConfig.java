package br.com.oficina.infrastructure.spring;

import br.com.oficina.application.port.out.BuscarEnderecoPorCepPort;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.port.out.EncerramentoRepositoryPort;
import br.com.oficina.application.port.out.EntregaRepositoryPort;
import br.com.oficina.application.port.out.ExecucaoRepositoryPort;
import br.com.oficina.application.port.out.OrcamentoRepositoryPort;
import br.com.oficina.application.port.out.OrdemDeServicoRepositoryPort;
import br.com.oficina.application.port.out.PagamentoGatewayPort;
import br.com.oficina.application.port.out.PagamentoRepositoryPort;
import br.com.oficina.application.port.out.PecaRepositoryPort;
import br.com.oficina.application.port.out.ServicoRepositoryPort;
import br.com.oficina.application.port.out.VeiculoRepositoryPort;
import br.com.oficina.application.port.in.NotificarStatusOrdemServicoInputPort;
import br.com.oficina.application.usecase.CriarOrdemServicoCompletaUseCase;
import br.com.oficina.application.usecase.NotificarStatusOrdemServicoUseCase;
import br.com.oficina.application.usecase.OrdemDeServicoUseCase;
import br.com.oficina.application.port.out.EmailNotificacaoPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrdemDeServicoUseCaseConfig {

    @Bean
    OrdemDeServicoUseCase ordemDeServicoUseCase(
            OrdemDeServicoRepositoryPort osRepository,
            ClienteRepositoryPort clienteRepository,
            VeiculoRepositoryPort veiculoRepository,
            ServicoRepositoryPort servicoRepository,
            PecaRepositoryPort pecaRepository,
            OrcamentoRepositoryPort orcamentoRepository,
            ExecucaoRepositoryPort execucaoRepository,
            PagamentoRepositoryPort pagamentoRepository,
            EntregaRepositoryPort entregaRepository,
            EncerramentoRepositoryPort encerramentoRepository,
            PagamentoGatewayPort pagamentoGateway,
            NotificarStatusOrdemServicoInputPort notificarStatusOrdemServicoInputPort) {
        return new OrdemDeServicoUseCase(
            osRepository,
            clienteRepository,
            veiculoRepository,
            servicoRepository,
            pecaRepository,
            orcamentoRepository,
            execucaoRepository,
            pagamentoRepository,
            entregaRepository,
            encerramentoRepository,
            pagamentoGateway,
            notificarStatusOrdemServicoInputPort
        );
    }

    @Bean
    NotificarStatusOrdemServicoUseCase notificarStatusOrdemServicoUseCase(
            EmailNotificacaoPort emailNotificacaoPort,
            @Value("${oficina.notificacao.base-url:http://localhost:8080}") String baseUrl) {
        return new NotificarStatusOrdemServicoUseCase(emailNotificacaoPort, baseUrl);
    }

    @Bean
    CriarOrdemServicoCompletaUseCase criarOrdemServicoCompletaUseCase(
            OrdemDeServicoRepositoryPort osRepository,
            ClienteRepositoryPort clienteRepository,
            VeiculoRepositoryPort veiculoRepository,
            ServicoRepositoryPort servicoRepository,
            PecaRepositoryPort pecaRepository,
            ExecucaoRepositoryPort execucaoRepository,
            BuscarEnderecoPorCepPort buscarEnderecoPorCepPort,
            NotificarStatusOrdemServicoInputPort notificarStatusOrdemServicoInputPort) {
        return new CriarOrdemServicoCompletaUseCase(
            osRepository,
            clienteRepository,
            veiculoRepository,
            servicoRepository,
            pecaRepository,
            execucaoRepository,
            buscarEnderecoPorCepPort,
            notificarStatusOrdemServicoInputPort
        );
    }
}
