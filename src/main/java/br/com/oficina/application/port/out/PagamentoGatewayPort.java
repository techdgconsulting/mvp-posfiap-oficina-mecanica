package br.com.oficina.application.port.out;

import br.com.oficina.domain.valueobject.MetodoPagamento;
import java.math.BigDecimal;

public interface PagamentoGatewayPort {
    GatewayResponse processar(GatewayRequest request);

    record GatewayRequest(Long ordemServicoId, BigDecimal valor, MetodoPagamento metodo) {}

    record GatewayResponse(boolean aprovado, String transactionId, String mensagem) {
        public static GatewayResponse aprovado(String transactionId, String mensagem) {
            return new GatewayResponse(true, transactionId, mensagem);
        }

        public static GatewayResponse recusado(String transactionId, String mensagem) {
            return new GatewayResponse(false, transactionId, mensagem);
        }
    }
}
