package br.com.oficina.infrastructure.spring;

import br.com.oficina.application.port.in.BuscarEntregaPorIdInputPort;
import br.com.oficina.application.port.in.BuscarEntregaPorOrdemServicoInputPort;
import br.com.oficina.application.port.in.ConsultarStatusEntregaInputPort;
import br.com.oficina.application.port.in.CriarEntregaInputPort;
import br.com.oficina.application.port.in.LiberarVeiculoEntregaInputPort;
import br.com.oficina.application.port.in.RegistrarEntregaVeiculoInputPort;
import br.com.oficina.application.port.out.EntregaRepositoryPort;
import br.com.oficina.application.usecase.BuscarEntregaPorIdUseCase;
import br.com.oficina.application.usecase.BuscarEntregaPorOrdemServicoUseCase;
import br.com.oficina.application.usecase.ConsultarStatusEntregaUseCase;
import br.com.oficina.application.usecase.CriarEntregaUseCase;
import br.com.oficina.application.usecase.LiberarVeiculoEntregaUseCase;
import br.com.oficina.application.usecase.RegistrarEntregaVeiculoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EntregaUseCaseConfig {

    @Bean
    CriarEntregaInputPort criarEntregaInputPort(EntregaRepositoryPort entregaRepositoryPort) {
        return new CriarEntregaUseCase(entregaRepositoryPort);
    }

    @Bean
    BuscarEntregaPorIdInputPort buscarEntregaPorIdInputPort(EntregaRepositoryPort entregaRepositoryPort) {
        return new BuscarEntregaPorIdUseCase(entregaRepositoryPort);
    }

    @Bean
    BuscarEntregaPorOrdemServicoInputPort buscarEntregaPorOrdemServicoInputPort(
            EntregaRepositoryPort entregaRepositoryPort) {
        return new BuscarEntregaPorOrdemServicoUseCase(entregaRepositoryPort);
    }

    @Bean
    LiberarVeiculoEntregaInputPort liberarVeiculoEntregaInputPort(EntregaRepositoryPort entregaRepositoryPort) {
        return new LiberarVeiculoEntregaUseCase(entregaRepositoryPort);
    }

    @Bean
    RegistrarEntregaVeiculoInputPort registrarEntregaVeiculoInputPort(EntregaRepositoryPort entregaRepositoryPort) {
        return new RegistrarEntregaVeiculoUseCase(entregaRepositoryPort);
    }

    @Bean
    ConsultarStatusEntregaInputPort consultarStatusEntregaInputPort(EntregaRepositoryPort entregaRepositoryPort) {
        return new ConsultarStatusEntregaUseCase(entregaRepositoryPort);
    }
}
