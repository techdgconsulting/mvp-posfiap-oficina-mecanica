package br.com.oficina.application.usecase;

import br.com.oficina.application.command.FinalizarServicoExecucaoCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.FinalizarServicoExecucaoInputPort;
import br.com.oficina.application.port.out.ExecucaoRepositoryPort;
import br.com.oficina.application.query.ExecucaoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FinalizarServicoExecucaoUseCase implements FinalizarServicoExecucaoInputPort {

    private final ExecucaoRepositoryPort execucaoRepository;

    @Override
    public ExecucaoResult execute(FinalizarServicoExecucaoCommand command) {
        var execucao = execucaoRepository.buscarPorId(command.execucaoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Execucao nao encontrada: " + command.execucaoId()));
        execucao.finalizarServico();
        return ExecucaoResultMapper.toResult(execucaoRepository.salvar(execucao));
    }
}
