package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.BuscarExecucaoPorIdInputPort;
import br.com.oficina.application.port.out.ExecucaoRepositoryPort;
import br.com.oficina.application.query.ExecucaoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuscarExecucaoPorIdUseCase implements BuscarExecucaoPorIdInputPort {

    private final ExecucaoRepositoryPort execucaoRepository;

    @Override
    public ExecucaoResult execute(Long id) {
        return execucaoRepository.buscarPorId(id)
                .map(ExecucaoResultMapper::toResult)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Execucao nao encontrada: " + id));
    }
}
