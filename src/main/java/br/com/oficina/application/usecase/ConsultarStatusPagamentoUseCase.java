package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.ConsultarStatusPagamentoInputPort;
import br.com.oficina.application.port.out.PagamentoRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConsultarStatusPagamentoUseCase implements ConsultarStatusPagamentoInputPort {

    private final PagamentoRepositoryPort pagamentoRepository;

    @Override
    public String executeStatus(Long id) {
        return pagamentoRepository.buscarPorId(id)
                .map(pagamento -> pagamento.getStatus().name())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pagamento nao encontrado: " + id));
    }
}
