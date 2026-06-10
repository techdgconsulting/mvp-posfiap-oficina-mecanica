package br.com.oficina.domain.estoque.vo;

import org.junit.jupiter.api.Test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Gestão de Estoque")
@Feature("Domínio — Value Object Quantidade")
class QuantidadeTest {

    @Test
    @Story("Criar quantidade com valor positivo")
    void deveCriarComValorPositivo() {
        var qtd = new Quantidade(10);
        assertEquals(10, qtd.getValor());
        assertTrue(qtd.validar());
    }

    @Test
    @Story("Criar quantidade com valor zero")
    void deveCriarComValorZero() {
        var qtd = new Quantidade(0);
        assertEquals(0, qtd.getValor());
    }

    @Test
    @Story("Rejeitar quantidade negativa")
    void naoDeveCriarComValorNegativo() {
        assertThrows(IllegalArgumentException.class, () -> new Quantidade(-1));
    }

    @Test
    @Story("Subtrair quantidade")
    void deveSubtrair() {
        var qtd = new Quantidade(10);
        var resultado = qtd.subtrair(3);
        assertEquals(7, resultado.getValor());
        assertEquals(10, qtd.getValor()); // imutável — original não muda
    }

    @Test
    @Story("Rejeitar subtração maior que o disponível")
    void naoDeveSubtrairMaisQueDisponivel() {
        var qtd = new Quantidade(5);
        assertThrows(IllegalStateException.class, () -> qtd.subtrair(10));
    }

    @Test
    @Story("Rejeitar subtração de valor zero ou negativo")
    void naoDeveSubtrairValorZeroOuNegativo() {
        var qtd = new Quantidade(10);
        assertThrows(IllegalArgumentException.class, () -> qtd.subtrair(0));
        assertThrows(IllegalArgumentException.class, () -> qtd.subtrair(-1));
    }

    @Test
    @Story("Adicionar quantidade")
    void deveAdicionar() {
        var qtd = new Quantidade(5);
        var resultado = qtd.adicionar(10);
        assertEquals(15, resultado.getValor());
        assertEquals(5, qtd.getValor()); // imutável
    }

    @Test
    @Story("Rejeitar adição de valor zero ou negativo")
    void naoDeveAdicionarValorZeroOuNegativo() {
        var qtd = new Quantidade(10);
        assertThrows(IllegalArgumentException.class, () -> qtd.adicionar(0));
        assertThrows(IllegalArgumentException.class, () -> qtd.adicionar(-1));
    }

    @Test
    @Story("Verificar disponibilidade de estoque")
    void deveVerificarDisponibilidade() {
        var qtd = new Quantidade(10);
        assertTrue(qtd.temDisponibilidade(5));
        assertTrue(qtd.temDisponibilidade(10));
        assertFalse(qtd.temDisponibilidade(11));
    }

    @Test
    void quantidadesIguaisDevemSerEquals() {
        var q1 = new Quantidade(10);
        var q2 = new Quantidade(10);
        assertEquals(q1, q2);
        assertEquals(q1.hashCode(), q2.hashCode());
    }

    @Test
    void quantidadesDiferentesNaoDevemSerEquals() {
        var q1 = new Quantidade(10);
        var q2 = new Quantidade(20);
        assertNotEquals(q1, q2);
    }

    @Test
    @Story("Converter quantidade para string")
    void deveConverterParaString() {
        var qtd = new Quantidade(42);
        assertEquals("42", qtd.toString());
    }
}
