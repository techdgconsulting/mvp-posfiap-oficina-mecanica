package br.com.oficina.infrastructure.spring;

import br.com.oficina.application.port.in.AtualizarMecanicoExecucaoInputPort;
import br.com.oficina.application.port.in.BuscarExecucaoPorIdInputPort;
import br.com.oficina.application.port.in.BuscarExecucaoPorOrdemServicoInputPort;
import br.com.oficina.application.port.in.CriarExecucaoInputPort;
import br.com.oficina.application.port.in.FinalizarServicoExecucaoInputPort;
import br.com.oficina.application.port.in.IniciarServicoExecucaoInputPort;
import br.com.oficina.application.port.in.ListarExecucoesPorStatusInputPort;
import br.com.oficina.application.port.in.RegistrarDiagnosticoExecucaoInputPort;
import br.com.oficina.application.port.out.ExecucaoRepositoryPort;
import br.com.oficina.application.usecase.AtualizarMecanicoExecucaoUseCase;
import br.com.oficina.application.usecase.BuscarExecucaoPorIdUseCase;
import br.com.oficina.application.usecase.BuscarExecucaoPorOrdemServicoUseCase;
import br.com.oficina.application.usecase.CriarExecucaoUseCase;
import br.com.oficina.application.usecase.FinalizarServicoExecucaoUseCase;
import br.com.oficina.application.usecase.IniciarServicoExecucaoUseCase;
import br.com.oficina.application.usecase.ListarExecucoesPorStatusUseCase;
import br.com.oficina.application.usecase.RegistrarDiagnosticoExecucaoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecucaoUseCaseConfig {

    @Bean
    CriarExecucaoInputPort criarExecucaoInputPort(ExecucaoRepositoryPort execucaoRepositoryPort) {
        return new CriarExecucaoUseCase(execucaoRepositoryPort);
    }

    @Bean
    BuscarExecucaoPorIdInputPort buscarExecucaoPorIdInputPort(ExecucaoRepositoryPort execucaoRepositoryPort) {
        return new BuscarExecucaoPorIdUseCase(execucaoRepositoryPort);
    }

    @Bean
    BuscarExecucaoPorOrdemServicoInputPort buscarExecucaoPorOrdemServicoInputPort(
            ExecucaoRepositoryPort execucaoRepositoryPort) {
        return new BuscarExecucaoPorOrdemServicoUseCase(execucaoRepositoryPort);
    }

    @Bean
    IniciarServicoExecucaoInputPort iniciarServicoExecucaoInputPort(ExecucaoRepositoryPort execucaoRepositoryPort) {
        return new IniciarServicoExecucaoUseCase(execucaoRepositoryPort);
    }

    @Bean
    FinalizarServicoExecucaoInputPort finalizarServicoExecucaoInputPort(ExecucaoRepositoryPort execucaoRepositoryPort) {
        return new FinalizarServicoExecucaoUseCase(execucaoRepositoryPort);
    }

    @Bean
    RegistrarDiagnosticoExecucaoInputPort registrarDiagnosticoExecucaoInputPort(
            ExecucaoRepositoryPort execucaoRepositoryPort) {
        return new RegistrarDiagnosticoExecucaoUseCase(execucaoRepositoryPort);
    }

    @Bean
    AtualizarMecanicoExecucaoInputPort atualizarMecanicoExecucaoInputPort(
            ExecucaoRepositoryPort execucaoRepositoryPort) {
        return new AtualizarMecanicoExecucaoUseCase(execucaoRepositoryPort);
    }

    @Bean
    ListarExecucoesPorStatusInputPort listarExecucoesPorStatusInputPort(
            ExecucaoRepositoryPort execucaoRepositoryPort) {
        return new ListarExecucoesPorStatusUseCase(execucaoRepositoryPort);
    }
}
