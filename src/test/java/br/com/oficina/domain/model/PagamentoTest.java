package br.com.oficina.domain.model;

import br.com.oficina.domain.valueobject.MetodoPagamento;
import br.com.oficina.domain.valueobject.StatusPagamento;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Faturamento")
@Feature("Domínio — Pagamento")
class PagamentoTest {

    @Test
    @Story("Criar pagamento com status PENDENTE")
    void deveCriarComStatusPendente() {
        var pagamento = Pagamento.builder()
                .ordemDeServicoId(1L)
                .valor(new BigDecimal("500.00"))
                .metodo(MetodoPagamento.PIX)
                .build();

        assertEquals(StatusPagamento.PENDENTE, pagamento.getStatus());
        assertNull(pagamento.getDataPagamento());
    }

    @Test
    @Story("Aprovar pagamento")
    void deveAprovarPagamento() {
        var pagamento = Pagamento.builder()
                .ordemDeServicoId(1L)
                .valor(new BigDecimal("500.00"))
                .metodo(MetodoPagamento.CARTAO_CREDITO)
                .build();

        pagamento.aprovar();

        assertEquals(StatusPagamento.APROVADO, pagamento.getStatus());
        assertNotNull(pagamento.getDataPagamento());
    }

    @Test
    @Story("Rejeitar aprovação de pagamento já aprovado")
    void naoDeveAprovarJaAprovado() {
        var pagamento = Pagamento.builder()
                .ordemDeServicoId(1L)
                .valor(new BigDecimal("200.00"))
                .metodo(MetodoPagamento.DINHEIRO)
                .build();

        pagamento.aprovar();
        assertThrows(IllegalStateException.class, pagamento::aprovar);
    }

    @Test
    @Story("Recusar pagamento")
    void deveRecusarPagamento() {
        var pag = Pagamento.builder()
                .ordemDeServicoId(1L)
                .valor(new BigDecimal("300.00"))
                .metodo(MetodoPagamento.BOLETO)
                .build();

        pag.recusar();
        assertEquals(StatusPagamento.RECUSADO, pag.getStatus());
    }

    @Test
    @Story("Rejeitar recusa de pagamento já aprovado")
    void naoDeveRecusarJaAprovado() {
        var pag = Pagamento.builder()
                .ordemDeServicoId(1L)
                .valor(new BigDecimal("300.00"))
                .metodo(MetodoPagamento.PIX)
                .build();
        pag.aprovar();
        assertThrows(IllegalStateException.class, pag::recusar);
    }

    @Test
    @Story("Aprovar pagamento com dados do gateway")
    void deveAprovarComDadosDoGateway() {
        var pag = Pagamento.builder()
                .ordemDeServicoId(1L)
                .valor(new BigDecimal("150.00"))
                .metodo(MetodoPagamento.PIX)
                .build();

        pag.aprovar("MOCK-tx-123", "Pagamento autorizado");

        assertEquals(StatusPagamento.APROVADO, pag.getStatus());
        assertEquals("MOCK-tx-123", pag.getTransactionId());
        assertEquals("Pagamento autorizado", pag.getGatewayMensagem());
        assertNotNull(pag.getDataPagamento());
    }

    @Test
    @Story("Recusar pagamento com dados do gateway")
    void deveRecusarComDadosDoGateway() {
        var pag = Pagamento.builder()
                .ordemDeServicoId(1L)
                .valor(new BigDecimal("150.00"))
                .metodo(MetodoPagamento.CARTAO_CREDITO)
                .build();

        pag.recusar("MOCK-tx-999", "saldo insuficiente");

        assertEquals(StatusPagamento.RECUSADO, pag.getStatus());
        assertEquals("MOCK-tx-999", pag.getTransactionId());
        assertEquals("saldo insuficiente", pag.getGatewayMensagem());
    }
}
