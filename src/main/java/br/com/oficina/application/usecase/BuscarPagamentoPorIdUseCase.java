package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.BuscarPagamentoPorIdInputPort;
import br.com.oficina.application.port.out.PagamentoRepositoryPort;
import br.com.oficina.application.query.PagamentoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuscarPagamentoPorIdUseCase implements BuscarPagamentoPorIdInputPort {

    private final PagamentoRepositoryPort pagamentoRepository;

    @Override
    public PagamentoResult execute(Long id) {
        return pagamentoRepository.buscarPorId(id)
                .map(PagamentoResultMapper::toResult)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pagamento nao encontrado: " + id));
    }
}
