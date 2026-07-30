package br.com.oficina.application.usecase;

import br.com.oficina.application.command.AtualizarMecanicoExecucaoCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.AtualizarMecanicoExecucaoInputPort;
import br.com.oficina.application.port.out.ExecucaoRepositoryPort;
import br.com.oficina.application.query.ExecucaoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AtualizarMecanicoExecucaoUseCase implements AtualizarMecanicoExecucaoInputPort {

    private final ExecucaoRepositoryPort execucaoRepository;

    @Override
    public ExecucaoResult execute(AtualizarMecanicoExecucaoCommand command) {
        var execucao = execucaoRepository.buscarPorId(command.execucaoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Execucao nao encontrada: " + command.execucaoId()));
        execucao.atualizarMecanicoResponsavel(command.mecanicoNome());
        return ExecucaoResultMapper.toResult(execucaoRepository.salvar(execucao));
    }
}
