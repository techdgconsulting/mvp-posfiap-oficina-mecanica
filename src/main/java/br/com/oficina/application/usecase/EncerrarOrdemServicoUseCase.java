package br.com.oficina.application.usecase;

import br.com.oficina.application.command.EncerrarOrdemServicoCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.EncerrarOrdemServicoInputPort;
import br.com.oficina.application.port.out.EncerramentoRepositoryPort;
import br.com.oficina.application.query.EncerramentoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EncerrarOrdemServicoUseCase implements EncerrarOrdemServicoInputPort {

    private final EncerramentoRepositoryPort encerramentoRepository;

    @Override
    public EncerramentoResult execute(EncerrarOrdemServicoCommand command) {
        var encerramento = encerramentoRepository.buscarPorId(command.encerramentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Encerramento nao encontrado: " + command.encerramentoId()));
        encerramento.encerrar();
        return EncerramentoResultMapper.toResult(encerramentoRepository.salvar(encerramento));
    }
}
