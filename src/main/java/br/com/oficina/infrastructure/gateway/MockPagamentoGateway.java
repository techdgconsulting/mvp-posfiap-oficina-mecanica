package br.com.oficina.infrastructure.gateway;

import br.com.oficina.domain.financeiro.PagamentoGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementação mock de gateway de pagamento (adapter).
 */
@Component
@Slf4j
public class MockPagamentoGateway implements PagamentoGateway {

    private final double approvalRate;
    private final long latencyMs;

    public MockPagamentoGateway(
            @Value("${oficina.pagamento.gateway.approval-rate:0.9}") double approvalRate,
            @Value("${oficina.pagamento.gateway.latency-ms:100}") long latencyMs) {
        this.approvalRate = approvalRate;
        this.latencyMs = latencyMs;
    }

    @Override
    public GatewayResponse processar(GatewayRequest request) {
        log.info("[MockGateway] Processando pagamento OS #{} valor R$ {} via {}",
                request.ordemServicoId(), request.valor(), request.metodo());

        simularLatencia();

        var transactionId = "MOCK-" + UUID.randomUUID();
        var aprovado = ThreadLocalRandom.current().nextDouble() < approvalRate;

        if (aprovado) {
            log.info("[MockGateway] APROVADO tx={}", transactionId);
            return GatewayResponse.aprovado(transactionId, "Pagamento autorizado pelo provedor mock");
        }
        log.warn("[MockGateway] RECUSADO tx={}", transactionId);
        return GatewayResponse.recusado(transactionId, "Pagamento recusado pelo provedor mock (saldo/limite insuficiente)");
    }

    private void simularLatencia() {
        if (latencyMs <= 0) return;
        try {
            Thread.sleep(latencyMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
