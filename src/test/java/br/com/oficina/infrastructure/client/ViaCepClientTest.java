package br.com.oficina.infrastructure.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

@ExtendWith(MockitoExtension.class)
@Epic("Integrações Externas")
@Feature("ViaCEP")
class ViaCepClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ViaCepClient viaCepClient;

    @Test
    @Story("Retornar endereço para CEP válido")
    void deveRetornarEnderecoParaCepValido() {
        var expected = new ViaCepResponse("01001-000", "Praça da Sé", "Sé", "São Paulo", "SP", null);
        when(restTemplate.getForObject(anyString(), eq(ViaCepResponse.class), eq("01001000")))
                .thenReturn(expected);

        var result = viaCepClient.buscarPorCep("01001000");

        assertTrue(result.isPresent());
        assertEquals("Praça da Sé", result.get().logradouro());
        assertEquals("São Paulo", result.get().localidade());
    }

    @Test
    @Story("Retornar vazio para CEP inválido")
    void deveRetornarVazioParaCepInvalido() {
        var erroResponse = new ViaCepResponse(null, null, null, null, null, true);
        when(restTemplate.getForObject(anyString(), eq(ViaCepResponse.class), eq("00000000")))
                .thenReturn(erroResponse);

        var result = viaCepClient.buscarPorCep("00000000");

        assertTrue(result.isEmpty());
    }

    @Test
    @Story("Retornar vazio para CEP com tamanho errado")
    void deveRetornarVazioParaCepComTamanhoErrado() {
        var result = viaCepClient.buscarPorCep("123");

        assertTrue(result.isEmpty());
    }

    @Test
    @Story("Retornar vazio quando API retorna nulo")
    void deveRetornarVazioQuandoApiRetornaNulo() {
        when(restTemplate.getForObject(anyString(), eq(ViaCepResponse.class), eq("01001000")))
                .thenReturn(null);

        var result = viaCepClient.buscarPorCep("01001000");

        assertTrue(result.isEmpty());
    }

    @Test
    @Story("Retornar vazio quando API lança exceção")
    void deveRetornarVazioQuandoApiLancaExcecao() {
        when(restTemplate.getForObject(anyString(), eq(ViaCepResponse.class), eq("01001000")))
                .thenThrow(new RestClientException("Connection refused"));

        var result = viaCepClient.buscarPorCep("01001-000");

        assertTrue(result.isEmpty());
    }

    @Test
    @Story("Limpar caracteres não numéricos do CEP")
    void deveLimparCaracteresNaoNumericosDoCep() {
        var expected = new ViaCepResponse("01001-000", "Praça da Sé", "Sé", "São Paulo", "SP", null);
        when(restTemplate.getForObject(anyString(), eq(ViaCepResponse.class), eq("01001000")))
                .thenReturn(expected);

        var result = viaCepClient.buscarPorCep("01001-000");

        assertTrue(result.isPresent());
        assertEquals("São Paulo", result.get().localidade());
    }
}
