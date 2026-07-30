package br.com.oficina.application.usecase;

import br.com.oficina.application.command.CriarExecucaoCommand;
import br.com.oficina.application.port.in.CriarExecucaoInputPort;
import br.com.oficina.application.port.out.ExecucaoRepositoryPort;
import br.com.oficina.application.query.ExecucaoResult;
import br.com.oficina.domain.model.Execucao;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CriarExecucaoUseCase implements CriarExecucaoInputPort {

    private final ExecucaoRepositoryPort execucaoRepository;

    @Override
    public ExecucaoResult execute(CriarExecucaoCommand command) {
        return ExecucaoResultMapper.toResult(execucaoRepository.salvar(Execucao.criar(command.ordemServicoId())));
    }
}
