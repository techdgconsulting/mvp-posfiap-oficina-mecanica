package br.com.oficina.application.usecase;

import br.com.oficina.application.command.AprovarPagamentoCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.AprovarPagamentoInputPort;
import br.com.oficina.application.port.out.PagamentoRepositoryPort;
import br.com.oficina.application.query.PagamentoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AprovarPagamentoUseCase implements AprovarPagamentoInputPort {

    private final PagamentoRepositoryPort pagamentoRepository;

    @Override
    public PagamentoResult execute(AprovarPagamentoCommand command) {
        var pagamento = pagamentoRepository.buscarPorId(command.pagamentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pagamento nao encontrado: " + command.pagamentoId()));
        pagamento.aprovar(command.transactionId(), command.gatewayMensagem());
        return PagamentoResultMapper.toResult(pagamentoRepository.salvar(pagamento));
    }
}
