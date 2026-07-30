package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.BuscarExecucaoPorOrdemServicoInputPort;
import br.com.oficina.application.port.out.ExecucaoRepositoryPort;
import br.com.oficina.application.query.ExecucaoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuscarExecucaoPorOrdemServicoUseCase implements BuscarExecucaoPorOrdemServicoInputPort {

    private final ExecucaoRepositoryPort execucaoRepository;

    @Override
    public ExecucaoResult executeByOrdemServico(Long ordemServicoId) {
        return execucaoRepository.buscarPorOrdemDeServico(ordemServicoId)
                .map(ExecucaoResultMapper::toResult)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Execucao nao encontrada para OS: " + ordemServicoId));
    }
}
