package br.com.oficina.application.usecase;

import br.com.oficina.application.command.IniciarServicoExecucaoCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.IniciarServicoExecucaoInputPort;
import br.com.oficina.application.port.out.ExecucaoRepositoryPort;
import br.com.oficina.application.query.ExecucaoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IniciarServicoExecucaoUseCase implements IniciarServicoExecucaoInputPort {

    private final ExecucaoRepositoryPort execucaoRepository;

    @Override
    public ExecucaoResult execute(IniciarServicoExecucaoCommand command) {
        var execucao = execucaoRepository.buscarPorId(command.execucaoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Execucao nao encontrada: " + command.execucaoId()));
        execucao.iniciarServico();
        return ExecucaoResultMapper.toResult(execucaoRepository.salvar(execucao));
    }
}
