package br.com.oficina.domain.atendimento.veiculo.vo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Atendimento ao Cliente")
@Feature("Domínio — Placa do Veículo")
class PlacaTest {

    @Test
    @Story("Aceitar placa no formato Mercosul")
    void deveAceitarPlacaMercosul() {
        var placa = new Placa("ABC1D23");
        assertEquals("ABC1D23", placa.getValor());
    }

    @Test
    @Story("Aceitar placa antiga como Mercosul")
    void deveAceitarPlacaAntigaComoMercosul() {
        var placa = new Placa("ABC1234");
        assertEquals("ABC1234", placa.getValor());
    }

    @Test
    @Story("Converter placa para uppercase")
    void deveConverterParaUppercase() {
        var placa = new Placa("abc1d23");
        assertEquals("ABC1D23", placa.getValor());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234567", "ABCDEFG", "AB1C234", ""})
    @Story("Rejeitar placa inválida")
    void deveRejeitarPlacaInvalida(String valor) {
        assertThrows(IllegalArgumentException.class, () -> new Placa(valor));
    }

    @Test
    @Story("Formatar placa")
    void deveFormatar() {
        var placa = new Placa("ABC1D23");
        assertEquals("ABC-1D23", placa.formatado());
    }
}
