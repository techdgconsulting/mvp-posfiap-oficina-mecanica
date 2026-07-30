package br.com.oficina.application.usecase;

import br.com.oficina.application.command.CriarEncerramentoCommand;
import br.com.oficina.application.port.in.CriarEncerramentoInputPort;
import br.com.oficina.application.port.out.EncerramentoRepositoryPort;
import br.com.oficina.application.query.EncerramentoResult;
import br.com.oficina.domain.model.Encerramento;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CriarEncerramentoUseCase implements CriarEncerramentoInputPort {

    private final EncerramentoRepositoryPort encerramentoRepository;

    @Override
    public EncerramentoResult execute(CriarEncerramentoCommand command) {
        return EncerramentoResultMapper.toResult(
                encerramentoRepository.salvar(Encerramento.criar(command.ordemServicoId())));
    }
}
