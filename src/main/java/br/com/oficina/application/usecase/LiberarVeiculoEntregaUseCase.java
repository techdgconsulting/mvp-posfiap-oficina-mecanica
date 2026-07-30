package br.com.oficina.application.usecase;

import br.com.oficina.application.command.LiberarVeiculoEntregaCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.LiberarVeiculoEntregaInputPort;
import br.com.oficina.application.port.out.EntregaRepositoryPort;
import br.com.oficina.application.query.EntregaResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LiberarVeiculoEntregaUseCase implements LiberarVeiculoEntregaInputPort {

    private final EntregaRepositoryPort entregaRepository;

    @Override
    public EntregaResult execute(LiberarVeiculoEntregaCommand command) {
        var entrega = entregaRepository.buscarPorId(command.entregaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Entrega nao encontrada: " + command.entregaId()));
        entrega.liberarVeiculo();
        return EntregaResultMapper.toResult(entregaRepository.salvar(entrega));
    }
}
