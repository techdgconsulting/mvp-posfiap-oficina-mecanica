package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.BuscarEncerramentoPorOrdemServicoInputPort;
import br.com.oficina.application.port.out.EncerramentoRepositoryPort;
import br.com.oficina.application.query.EncerramentoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuscarEncerramentoPorOrdemServicoUseCase implements BuscarEncerramentoPorOrdemServicoInputPort {

    private final EncerramentoRepositoryPort encerramentoRepository;

    @Override
    public EncerramentoResult executeByOrdemServico(Long ordemServicoId) {
        return encerramentoRepository.buscarPorOrdemDeServico(ordemServicoId)
                .map(EncerramentoResultMapper::toResult)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Encerramento nao encontrado para OS: " + ordemServicoId));
    }
}
