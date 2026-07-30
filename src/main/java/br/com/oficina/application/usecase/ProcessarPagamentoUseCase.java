package br.com.oficina.application.usecase;

import br.com.oficina.application.command.ProcessarPagamentoCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.ProcessarPagamentoInputPort;
import br.com.oficina.application.port.out.PagamentoGatewayPort;
import br.com.oficina.application.port.out.PagamentoRepositoryPort;
import br.com.oficina.application.query.PagamentoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProcessarPagamentoUseCase implements ProcessarPagamentoInputPort {

    private final PagamentoRepositoryPort pagamentoRepository;
    private final PagamentoGatewayPort pagamentoGateway;

    @Override
    public PagamentoResult execute(ProcessarPagamentoCommand command) {
        var pagamento = pagamentoRepository.buscarPorId(command.pagamentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pagamento nao encontrado: " + command.pagamentoId()));

        var response = pagamentoGateway.processar(new PagamentoGatewayPort.GatewayRequest(
                pagamento.getOrdemDeServicoId(),
                pagamento.getValor(),
                pagamento.getMetodo()));

        if (response.aprovado()) {
            pagamento.aprovar(response.transactionId(), response.mensagem());
        } else {
            pagamento.recusar(response.transactionId(), response.mensagem());
        }
        return PagamentoResultMapper.toResult(pagamentoRepository.salvar(pagamento));
    }
}
