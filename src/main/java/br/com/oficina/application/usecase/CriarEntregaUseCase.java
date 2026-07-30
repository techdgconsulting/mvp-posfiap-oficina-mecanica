package br.com.oficina.application.usecase;

import br.com.oficina.application.command.CriarEntregaCommand;
import br.com.oficina.application.port.in.CriarEntregaInputPort;
import br.com.oficina.application.port.out.EntregaRepositoryPort;
import br.com.oficina.application.query.EntregaResult;
import br.com.oficina.domain.model.Entrega;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CriarEntregaUseCase implements CriarEntregaInputPort {

    private final EntregaRepositoryPort entregaRepository;

    @Override
    public EntregaResult execute(CriarEntregaCommand command) {
        return EntregaResultMapper.toResult(entregaRepository.salvar(Entrega.criar(command.ordemServicoId())));
    }
}
