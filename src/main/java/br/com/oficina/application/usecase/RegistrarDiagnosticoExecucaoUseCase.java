package br.com.oficina.application.usecase;

import br.com.oficina.application.command.RegistrarDiagnosticoExecucaoCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.RegistrarDiagnosticoExecucaoInputPort;
import br.com.oficina.application.port.out.ExecucaoRepositoryPort;
import br.com.oficina.application.query.ExecucaoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegistrarDiagnosticoExecucaoUseCase implements RegistrarDiagnosticoExecucaoInputPort {

    private final ExecucaoRepositoryPort execucaoRepository;

    @Override
    public ExecucaoResult execute(RegistrarDiagnosticoExecucaoCommand command) {
        var execucao = execucaoRepository.buscarPorId(command.execucaoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Execucao nao encontrada: " + command.execucaoId()));
        execucao.registrarDiagnostico(command.descricaoProblema());
        return ExecucaoResultMapper.toResult(execucaoRepository.salvar(execucao));
    }
}
