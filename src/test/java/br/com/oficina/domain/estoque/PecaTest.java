package br.com.oficina.domain.estoque;

import br.com.oficina.domain.estoque.vo.Quantidade;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Gestão de Estoque")
@Feature("Domínio — Peça")
class PecaTest {

    private Peca criarPeca(int estoque) {
        return Peca.builder()
                .nome("Filtro de Ar")
                .descricao("Filtro de ar do motor")
                .quantidadeEstoque(new Quantidade(estoque))
                .valorUnitario(new BigDecimal("45.00"))
                .build();
    }

    @Test
    @Story("Baixar estoque de peça")
    void deveBaixarEstoque() {
        var peca = criarPeca(10);
        peca.baixarEstoque(3);
        assertEquals(7, peca.getQuantidadeEstoqueValor());
    }

    @Test
    @Story("Rejeitar baixa maior que o estoque disponível")
    void naoDeveBaixarMaisQueDisponivel() {
        var peca = criarPeca(2);
        assertThrows(IllegalStateException.class, () -> peca.baixarEstoque(5));
    }

    @Test
    @Story("Rejeitar baixa de quantidade negativa")
    void naoDeveBaixarQuantidadeNegativa() {
        var peca = criarPeca(10);
        assertThrows(IllegalArgumentException.class, () -> peca.baixarEstoque(0));
    }

    @Test
    @Story("Repor estoque de peça")
    void deveReporEstoque() {
        var peca = criarPeca(5);
        peca.reporEstoque(10);
        assertEquals(15, peca.getQuantidadeEstoqueValor());
    }

    @Test
    @Story("Rejeitar reposição com quantidade negativa")
    void naoDeveReporQuantidadeNegativa() {
        var peca = criarPeca(5);
        assertThrows(IllegalArgumentException.class, () -> peca.reporEstoque(-1));
    }

    @Test
    @Story("Verificar disponibilidade de peça")
    void deveVerificarDisponibilidade() {
        var peca = criarPeca(10);
        assertTrue(peca.temDisponibilidade(5));
        assertFalse(peca.temDisponibilidade(15));
    }

    @Test
    @Story("Atualizar dados da peça")
    void deveAtualizarDados() {
        var peca = criarPeca(10);
        peca.atualizar("Filtro Novo", "Atualizado", new BigDecimal("55.00"), 15);
        assertEquals("Filtro Novo", peca.getNome());
        assertEquals(new BigDecimal("55.00"), peca.getValorUnitario());
        assertEquals(15, peca.getEstoqueMinimo());
    }

    @Test
    @Story("Identificar estoque baixo")
    void deveIdentificarEstoqueBaixo() {
        var peca = Peca.builder()
                .nome("Filtro")
                .descricao("Desc")
                .quantidadeEstoque(new Quantidade(3))
                .valorUnitario(new BigDecimal("10.00"))
                .estoqueMinimo(5)
                .build();
        assertTrue(peca.estaComEstoqueBaixo());
    }

    @Test
    @Story("Identificar estoque normal")
    void deveIdentificarEstoqueNormal() {
        var peca = Peca.builder()
                .nome("Filtro")
                .descricao("Desc")
                .quantidadeEstoque(new Quantidade(20))
                .valorUnitario(new BigDecimal("10.00"))
                .estoqueMinimo(5)
                .build();
        assertFalse(peca.estaComEstoqueBaixo());
    }
}
