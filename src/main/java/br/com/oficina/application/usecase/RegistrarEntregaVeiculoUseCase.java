package br.com.oficina.application.usecase;

import br.com.oficina.application.command.RegistrarEntregaVeiculoCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.RegistrarEntregaVeiculoInputPort;
import br.com.oficina.application.port.out.EntregaRepositoryPort;
import br.com.oficina.application.query.EntregaResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegistrarEntregaVeiculoUseCase implements RegistrarEntregaVeiculoInputPort {

    private final EntregaRepositoryPort entregaRepository;

    @Override
    public EntregaResult execute(RegistrarEntregaVeiculoCommand command) {
        var entrega = entregaRepository.buscarPorId(command.entregaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Entrega nao encontrada: " + command.entregaId()));
        entrega.entregarVeiculo();
        return EntregaResultMapper.toResult(entregaRepository.salvar(entrega));
    }
}
