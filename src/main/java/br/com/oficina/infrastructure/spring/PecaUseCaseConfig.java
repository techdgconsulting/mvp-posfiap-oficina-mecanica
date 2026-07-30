package br.com.oficina.infrastructure.spring;

import br.com.oficina.application.port.in.AtualizarPecaInputPort;
import br.com.oficina.application.port.in.BaixarEstoqueInputPort;
import br.com.oficina.application.port.in.BuscarPecaPorIdInputPort;
import br.com.oficina.application.port.in.CriarPecaInputPort;
import br.com.oficina.application.port.in.ExcluirPecaInputPort;
import br.com.oficina.application.port.in.ListarPecasComEstoqueBaixoInputPort;
import br.com.oficina.application.port.in.ListarPecasInputPort;
import br.com.oficina.application.port.in.ReporEstoqueInputPort;
import br.com.oficina.application.port.in.VerificarDisponibilidadePecaInputPort;
import br.com.oficina.application.port.out.PecaRepositoryPort;
import br.com.oficina.application.usecase.AtualizarPecaUseCase;
import br.com.oficina.application.usecase.BaixarEstoqueUseCase;
import br.com.oficina.application.usecase.BuscarPecaPorIdUseCase;
import br.com.oficina.application.usecase.CriarPecaUseCase;
import br.com.oficina.application.usecase.ExcluirPecaUseCase;
import br.com.oficina.application.usecase.ListarPecasComEstoqueBaixoUseCase;
import br.com.oficina.application.usecase.ListarPecasUseCase;
import br.com.oficina.application.usecase.ReporEstoqueUseCase;
import br.com.oficina.application.usecase.VerificarDisponibilidadePecaUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PecaUseCaseConfig {

    @Bean
    CriarPecaInputPort criarPecaInputPort(PecaRepositoryPort pecaRepositoryPort) {
        return new CriarPecaUseCase(pecaRepositoryPort);
    }

    @Bean
    AtualizarPecaInputPort atualizarPecaInputPort(PecaRepositoryPort pecaRepositoryPort) {
        return new AtualizarPecaUseCase(pecaRepositoryPort);
    }

    @Bean
    BuscarPecaPorIdInputPort buscarPecaPorIdInputPort(PecaRepositoryPort pecaRepositoryPort) {
        return new BuscarPecaPorIdUseCase(pecaRepositoryPort);
    }

    @Bean
    ListarPecasInputPort listarPecasInputPort(PecaRepositoryPort pecaRepositoryPort) {
        return new ListarPecasUseCase(pecaRepositoryPort);
    }

    @Bean
    ListarPecasComEstoqueBaixoInputPort listarPecasComEstoqueBaixoInputPort(PecaRepositoryPort pecaRepositoryPort) {
        return new ListarPecasComEstoqueBaixoUseCase(pecaRepositoryPort);
    }

    @Bean
    ExcluirPecaInputPort excluirPecaInputPort(PecaRepositoryPort pecaRepositoryPort) {
        return new ExcluirPecaUseCase(pecaRepositoryPort);
    }

    @Bean
    ReporEstoqueInputPort reporEstoqueInputPort(PecaRepositoryPort pecaRepositoryPort) {
        return new ReporEstoqueUseCase(pecaRepositoryPort);
    }

    @Bean
    BaixarEstoqueInputPort baixarEstoqueInputPort(PecaRepositoryPort pecaRepositoryPort) {
        return new BaixarEstoqueUseCase(pecaRepositoryPort);
    }

    @Bean
    VerificarDisponibilidadePecaInputPort verificarDisponibilidadePecaInputPort(PecaRepositoryPort pecaRepositoryPort) {
        return new VerificarDisponibilidadePecaUseCase(pecaRepositoryPort);
    }
}
