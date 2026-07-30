package br.com.oficina.application.usecase;

import br.com.oficina.application.port.in.ListarExecucoesPorStatusInputPort;
import br.com.oficina.application.port.out.ExecucaoRepositoryPort;
import br.com.oficina.application.query.ExecucaoResult;
import br.com.oficina.domain.valueobject.StatusExecucao;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListarExecucoesPorStatusUseCase implements ListarExecucoesPorStatusInputPort {

    private final ExecucaoRepositoryPort execucaoRepository;

    @Override
    public List<ExecucaoResult> execute(StatusExecucao status) {
        return execucaoRepository.listarPorStatus(status).stream()
                .map(ExecucaoResultMapper::toResult)
                .toList();
    }
}
