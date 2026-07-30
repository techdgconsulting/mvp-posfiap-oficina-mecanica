package br.com.oficina.adapters.out.payment;

import br.com.oficina.application.port.out.PagamentoGatewayPort;
import br.com.oficina.domain.valueobject.MetodoPagamento;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Integrações Externas")
@Feature("Gateway de Pagamento")
class MockPagamentoGatewayTest {

    @Test
    @Story("Aprovar pagamento quando taxa é 1.0")
    void deveAprovarQuandoTaxaForUm() {
        var gateway = new MockPagamentoGatewayAdapter(1.0, 0);
        var resp = gateway.processar(new PagamentoGatewayPort.GatewayRequest(
                1L, new BigDecimal("500.00"), MetodoPagamento.PIX));

        assertTrue(resp.aprovado());
        assertNotNull(resp.transactionId());
        assertTrue(resp.transactionId().startsWith("MOCK-"));
        assertNotNull(resp.mensagem());
    }

    @Test
    @Story("Recusar pagamento quando taxa é 0.0")
    void deveRecusarQuandoTaxaForZero() {
        var gateway = new MockPagamentoGatewayAdapter(0.0, 0);
        var resp = gateway.processar(new PagamentoGatewayPort.GatewayRequest(
                1L, new BigDecimal("100.00"), MetodoPagamento.CARTAO_CREDITO));

        assertFalse(resp.aprovado());
        assertNotNull(resp.transactionId());
        assertTrue(resp.mensagem().toLowerCase().contains("recusado"));
    }

    @Test
    @Story("Gerar transaction ID único por chamada")
    void deveGerarTransactionIdUnicoPorChamada() {
        var gateway = new MockPagamentoGatewayAdapter(1.0, 0);
        var r1 = gateway.processar(new PagamentoGatewayPort.GatewayRequest(1L, BigDecimal.TEN, MetodoPagamento.PIX));
        var r2 = gateway.processar(new PagamentoGatewayPort.GatewayRequest(1L, BigDecimal.TEN, MetodoPagamento.PIX));
        assertNotEquals(r1.transactionId(), r2.transactionId());
    }
}
