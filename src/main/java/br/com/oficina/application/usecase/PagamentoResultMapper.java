package br.com.oficina.application.usecase;

import br.com.oficina.application.query.PagamentoResult;
import br.com.oficina.domain.model.Pagamento;

final class PagamentoResultMapper {

    private PagamentoResultMapper() {
    }

    static PagamentoResult toResult(Pagamento pagamento) {
        return new PagamentoResult(
                pagamento.getId(),
                pagamento.getOrdemDeServicoId(),
                pagamento.getStatus().name(),
                pagamento.getMetodo().name(),
                pagamento.getValor(),
                pagamento.getDataPagamento(),
                pagamento.getTransactionId(),
                pagamento.getGatewayMensagem());
    }
}
