package br.com.oficina.application.usecase;

import br.com.oficina.application.command.RecusarPagamentoCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.RecusarPagamentoInputPort;
import br.com.oficina.application.port.out.PagamentoRepositoryPort;
import br.com.oficina.application.query.PagamentoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RecusarPagamentoUseCase implements RecusarPagamentoInputPort {

    private final PagamentoRepositoryPort pagamentoRepository;

    @Override
    public PagamentoResult execute(RecusarPagamentoCommand command) {
        var pagamento = pagamentoRepository.buscarPorId(command.pagamentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pagamento nao encontrado: " + command.pagamentoId()));
        pagamento.recusar(command.transactionId(), command.gatewayMensagem());
        return PagamentoResultMapper.toResult(pagamentoRepository.salvar(pagamento));
    }
}
