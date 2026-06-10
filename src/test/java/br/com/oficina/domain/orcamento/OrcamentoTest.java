package br.com.oficina.domain.orcamento;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Faturamento")
@Feature("Domínio — Orçamento")
class OrcamentoTest {

    @Test
    @Story("Gerar orçamento com status PENDENTE")
    void deveGerarOrcamentoComStatusPendente() {
        var orc = Orcamento.gerar(1L, new BigDecimal("500.00"));

        assertEquals(StatusOrcamento.PENDENTE, orc.getStatus());
        assertEquals(new BigDecimal("500.00"), orc.getValorTotal());
        assertNotNull(orc.getDataValidade());
    }

    @Test
    @Story("Rejeitar orçamento com valor zero")
    void naoDeveGerarComValorZero() {
        assertThrows(IllegalArgumentException.class,
            () -> Orcamento.gerar(1L, BigDecimal.ZERO));
    }

    @Test
    @Story("Rejeitar orçamento com valor negativo")
    void naoDeveGerarComValorNegativo() {
        assertThrows(IllegalArgumentException.class,
            () -> Orcamento.gerar(1L, new BigDecimal("-100")));
    }

    @Test
    @Story("Enviar e aprovar orçamento")
    void fluxoEnviarEAprovar() {
        var orc = Orcamento.gerar(1L, new BigDecimal("500.00"));

        orc.enviar();
        assertEquals(StatusOrcamento.ENVIADO, orc.getStatus());

        orc.aprovar();
        assertEquals(StatusOrcamento.APROVADO, orc.getStatus());
    }

    @Test
    @Story("Enviar e rejeitar orçamento")
    void fluxoEnviarERejeitar() {
        var orc = Orcamento.gerar(1L, new BigDecimal("300.00"));
        orc.enviar();
        orc.rejeitar();
        assertEquals(StatusOrcamento.REJEITADO, orc.getStatus());
    }

    @Test
    @Story("Rejeitar aprovação sem envio prévio")
    void naoDeveAprovarSemEnviar() {
        var orc = Orcamento.gerar(1L, new BigDecimal("500.00"));
        assertThrows(IllegalStateException.class, orc::aprovar);
    }

    @Test
    @Story("Rejeitar envio duplicado")
    void naoDeveEnviarDuasVezes() {
        var orc = Orcamento.gerar(1L, new BigDecimal("200.00"));
        orc.enviar();
        assertThrows(IllegalStateException.class, orc::enviar);
    }

    @Test
    @Story("Rejeitar orçamento com valor nulo")
    void naoDeveGerarComValorNulo() {
        assertThrows(IllegalArgumentException.class, () -> Orcamento.gerar(1L, null));
    }

    @Test
    @Story("Rejeitar rejeição sem envio prévio")
    void naoDeveRejeitarSemEnviar() {
        var orc = Orcamento.gerar(1L, new BigDecimal("500"));
        assertThrows(IllegalStateException.class, orc::rejeitar);
    }

    @Test
    @Story("Rejeitar aprovação de orçamento expirado")
    void naoDeveAprovarQuandoExpirado() {
        var orc = Orcamento.builder()
                .ordemDeServicoId(1L)
                .valorTotal(new BigDecimal("500"))
                .status(StatusOrcamento.ENVIADO)
                .dataCriacao(java.time.LocalDateTime.now().minusDays(30))
                .dataValidade(java.time.LocalDateTime.now().minusDays(1))
                .build();

        assertThrows(IllegalStateException.class, orc::aprovar);
    }

    @Test
    @Story("Retornar não expirado quando validade é nula")
    void deveRetornarNaoExpiradoComValidadeNula() {
        var orc = Orcamento.builder()
                .ordemDeServicoId(1L)
                .valorTotal(new BigDecimal("500"))
                .build();

        assertFalse(orc.estaExpirado());
    }

    @Test
    @Story("Retornar não expirado com data de validade futura")
    void estaExpiradoDeveRetornarFalseComDataFutura() {
        var orc = Orcamento.builder()
                .ordemDeServicoId(1L)
                .valorTotal(new BigDecimal("500"))
                .status(StatusOrcamento.ENVIADO)
                .dataCriacao(java.time.LocalDateTime.now())
                .dataValidade(java.time.LocalDateTime.now().plusDays(7))
                .build();

        assertFalse(orc.estaExpirado());
    }
}
