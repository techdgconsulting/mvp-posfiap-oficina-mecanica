package br.com.oficina.domain.atendimento.cliente.vo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Atendimento ao Cliente")
@Feature("Domínio — CPF/CNPJ")
class CpfCnpjTest {

    @Test
    @Story("Criar CPF válido")
    void deveCriarCpfValido() {
        var cpf = new CpfCnpj("529.982.247-25");
        assertEquals("52998224725", cpf.getValor());
        assertEquals(CpfCnpj.TipoDocumento.CPF, cpf.getTipo());
    }

    @Test
    @Story("Criar CNPJ válido")
    void deveCriarCnpjValido() {
        var cnpj = new CpfCnpj("11.222.333/0001-81");
        assertEquals("11222333000181", cnpj.getValor());
        assertEquals(CpfCnpj.TipoDocumento.CNPJ, cnpj.getTipo());
    }

    @Test
    @Story("Aceitar CPF sem formatação")
    void deveAceitarCpfSemFormatacao() {
        var cpf = new CpfCnpj("52998224725");
        assertEquals("52998224725", cpf.getValor());
    }

    @ParameterizedTest
    @ValueSource(strings = {"11111111111", "00000000000", "123", "abc", ""})
    @Story("Rejeitar CPF inválido")
    void deveRejeitarCpfInvalido(String doc) {
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj(doc));
    }

    @Test
    @Story("Rejeitar CPF com dígitos verificadores incorretos")
    void deveRejeitarCpfComDigitosErrados() {
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("529.982.247-99"));
    }

    @Test
    @Story("Rejeitar CNPJ inválido")
    void deveRejeitarCnpjInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("11.222.333/0001-99"));
    }

    @Test
    @Story("Formatar CPF")
    void deveFormatarCpf() {
        var cpf = new CpfCnpj("52998224725");
        assertEquals("529.982.247-25", cpf.formatado());
    }

    @Test
    @Story("Formatar CNPJ")
    void deveFormatarCnpj() {
        var cnpj = new CpfCnpj("11222333000181");
        assertEquals("11.222.333/0001-81", cnpj.formatado());
    }

    @Test
    void doisCpfsIguaisDevemSerEquals() {
        var cpf1 = new CpfCnpj("529.982.247-25");
        var cpf2 = new CpfCnpj("52998224725");
        assertEquals(cpf1, cpf2);
    }

    // testa branch primeiroDigito >= 10 no CPF (resto 1 → digito 0)
    @Test
    @Story("Criar CPF com primeiro dígito verificador zero")
    void deveCriarCpfComPrimeiroDigitoVerificadorZero() {
        var cpf = new CpfCnpj("12345678909");
        assertEquals("12345678909", cpf.getValor());
        assertEquals(CpfCnpj.TipoDocumento.CPF, cpf.getTipo());
    }

    // testa branch segundoDigito >= 10 no CPF
    @Test
    @Story("Criar CPF com segundo dígito verificador zero")
    void deveCriarCpfComSegundoDigitoVerificadorZero() {
        var cpf = new CpfCnpj("10000001090");
        assertEquals("10000001090", cpf.getValor());
    }

    // testa branch soma % 11 < 2 no primeiro digito do CNPJ
    @Test
    @Story("Criar CNPJ com primeiro dígito verificador zero por resto")
    void deveCriarCnpjComPrimeiroDigitoZeroPorResto() {
        var cnpj = new CpfCnpj("20010000000007");
        assertEquals("20010000000007", cnpj.getValor());
        assertEquals(CpfCnpj.TipoDocumento.CNPJ, cnpj.getTipo());
    }

    // testa branch soma % 11 < 2 no segundo digito do CNPJ
    @Test
    @Story("Criar CNPJ com segundo dígito verificador zero por resto")
    void deveCriarCnpjComSegundoDigitoZeroPorResto() {
        var cnpj = new CpfCnpj("20000000010080");
        assertEquals("20000000010080", cnpj.getValor());
    }

    @Test
    @Story("Rejeitar CNPJ com todos os dígitos iguais")
    void deveRejeitarCnpjComTodosDigitosIguais() {
        assertThrows(IllegalArgumentException.class, () -> new CpfCnpj("11111111111111"));
    }
}
