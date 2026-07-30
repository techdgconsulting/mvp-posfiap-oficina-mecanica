package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.BuscarPagamentoPorOrdemServicoInputPort;
import br.com.oficina.application.port.out.PagamentoRepositoryPort;
import br.com.oficina.application.query.PagamentoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuscarPagamentoPorOrdemServicoUseCase implements BuscarPagamentoPorOrdemServicoInputPort {

    private final PagamentoRepositoryPort pagamentoRepository;

    @Override
    public PagamentoResult executeByOrdemServico(Long ordemServicoId) {
        return pagamentoRepository.buscarPorOrdemDeServico(ordemServicoId)
                .map(PagamentoResultMapper::toResult)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Pagamento nao encontrado para OS: " + ordemServicoId));
    }
}
