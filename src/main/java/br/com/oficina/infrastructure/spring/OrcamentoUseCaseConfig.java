package br.com.oficina.infrastructure.spring;

import br.com.oficina.application.port.in.AprovarOrcamentoDiretoInputPort;
import br.com.oficina.application.port.in.AprovarOrcamentoInputPort;
import br.com.oficina.application.port.in.BuscarOrcamentoAtivoPorOrdemServicoInputPort;
import br.com.oficina.application.port.in.BuscarOrcamentoPorIdInputPort;
import br.com.oficina.application.port.in.ConsultarStatusOrcamentoInputPort;
import br.com.oficina.application.port.in.EnviarOrcamentoInputPort;
import br.com.oficina.application.port.in.ListarOrcamentosPorOrdemServicoInputPort;
import br.com.oficina.application.port.in.RejeitarOrcamentoDiretoInputPort;
import br.com.oficina.application.port.in.RejeitarOrcamentoInputPort;
import br.com.oficina.application.port.in.ValidarValidadeOrcamentoInputPort;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.port.out.EmailNotificacaoPort;
import br.com.oficina.application.port.out.OrcamentoDecisaoClienteRepositoryPort;
import br.com.oficina.application.port.out.OrcamentoRepositoryPort;
import br.com.oficina.application.port.out.OrdemDeServicoRepositoryPort;
import br.com.oficina.application.port.out.TokenSeguroPort;
import br.com.oficina.application.usecase.AprovarOrcamentoDiretoUseCase;
import br.com.oficina.application.usecase.BuscarOrcamentoAtivoPorOrdemServicoUseCase;
import br.com.oficina.application.usecase.BuscarOrcamentoPorIdUseCase;
import br.com.oficina.application.usecase.ConsultarStatusOrcamentoUseCase;
import br.com.oficina.application.usecase.EnviarOrcamentoUseCase;
import br.com.oficina.application.usecase.ListarOrcamentosPorOrdemServicoUseCase;
import br.com.oficina.application.usecase.OrcamentoDecisaoClienteUseCase;
import br.com.oficina.application.usecase.RejeitarOrcamentoDiretoUseCase;
import br.com.oficina.application.usecase.ValidarValidadeOrcamentoUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrcamentoUseCaseConfig {

    @Bean
    BuscarOrcamentoPorIdInputPort buscarOrcamentoPorIdInputPort(OrcamentoRepositoryPort orcamentoRepositoryPort) {
        return new BuscarOrcamentoPorIdUseCase(orcamentoRepositoryPort);
    }

    @Bean
    BuscarOrcamentoAtivoPorOrdemServicoInputPort buscarOrcamentoAtivoPorOrdemServicoInputPort(
            OrcamentoRepositoryPort orcamentoRepositoryPort) {
        return new BuscarOrcamentoAtivoPorOrdemServicoUseCase(orcamentoRepositoryPort);
    }

    @Bean
    ListarOrcamentosPorOrdemServicoInputPort listarOrcamentosPorOrdemServicoInputPort(
            OrcamentoRepositoryPort orcamentoRepositoryPort) {
        return new ListarOrcamentosPorOrdemServicoUseCase(orcamentoRepositoryPort);
    }

    @Bean
    EnviarOrcamentoInputPort enviarOrcamentoInputPort(OrcamentoRepositoryPort orcamentoRepositoryPort) {
        return new EnviarOrcamentoUseCase(orcamentoRepositoryPort);
    }

    @Bean
    AprovarOrcamentoDiretoInputPort aprovarOrcamentoDiretoInputPort(
            OrcamentoRepositoryPort orcamentoRepositoryPort) {
        return new AprovarOrcamentoDiretoUseCase(orcamentoRepositoryPort);
    }

    @Bean
    RejeitarOrcamentoDiretoInputPort rejeitarOrcamentoDiretoInputPort(
            OrcamentoRepositoryPort orcamentoRepositoryPort) {
        return new RejeitarOrcamentoDiretoUseCase(orcamentoRepositoryPort);
    }

    @Bean
    ConsultarStatusOrcamentoInputPort consultarStatusOrcamentoInputPort(
            OrcamentoRepositoryPort orcamentoRepositoryPort) {
        return new ConsultarStatusOrcamentoUseCase(orcamentoRepositoryPort);
    }

    @Bean
    ValidarValidadeOrcamentoInputPort validarValidadeOrcamentoInputPort(
            OrcamentoRepositoryPort orcamentoRepositoryPort) {
        return new ValidarValidadeOrcamentoUseCase(orcamentoRepositoryPort);
    }

    @Bean
    OrcamentoDecisaoClienteUseCase orcamentoDecisaoClienteUseCase(
            OrdemDeServicoRepositoryPort osRepository,
            ClienteRepositoryPort clienteRepository,
            OrcamentoRepositoryPort orcamentoRepository,
            OrcamentoDecisaoClienteRepositoryPort decisaoRepository,
            EmailNotificacaoPort emailNotificacaoPort,
            TokenSeguroPort tokenSeguroPort,
            AprovarOrcamentoInputPort aprovarOrcamentoInputPort,
            RejeitarOrcamentoInputPort rejeitarOrcamentoInputPort,
            @Value("${oficina.notificacao.base-url:http://localhost:8080}") String baseUrl,
            @Value("${oficina.notificacao.orcamento.expiracao-horas:48}") long expiracaoHoras) {
        return new OrcamentoDecisaoClienteUseCase(
                osRepository,
                clienteRepository,
                orcamentoRepository,
                decisaoRepository,
                emailNotificacaoPort,
                tokenSeguroPort,
                aprovarOrcamentoInputPort,
                rejeitarOrcamentoInputPort,
                baseUrl,
                expiracaoHoras);
    }

}
