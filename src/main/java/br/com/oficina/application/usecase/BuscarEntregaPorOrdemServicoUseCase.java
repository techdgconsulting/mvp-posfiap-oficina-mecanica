package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.BuscarEntregaPorOrdemServicoInputPort;
import br.com.oficina.application.port.out.EntregaRepositoryPort;
import br.com.oficina.application.query.EntregaResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuscarEntregaPorOrdemServicoUseCase implements BuscarEntregaPorOrdemServicoInputPort {

    private final EntregaRepositoryPort entregaRepository;

    @Override
    public EntregaResult executeByOrdemServico(Long ordemServicoId) {
        return entregaRepository.buscarPorOrdemDeServico(ordemServicoId)
                .map(EntregaResultMapper::toResult)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Entrega nao encontrada para OS: " + ordemServicoId));
    }
}
