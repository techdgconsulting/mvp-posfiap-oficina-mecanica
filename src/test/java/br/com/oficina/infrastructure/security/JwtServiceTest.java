package br.com.oficina.infrastructure.security;

import org.junit.jupiter.api.Test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Segurança e Autenticação")
@Feature("Serviço JWT")
class JwtServiceTest {

    private final JwtService jwtService = new JwtService("chave-secreta-para-testes-com-tamanho-suficiente", 3600000);

    @Test
    @Story("Gerar e validar token JWT")
    void deveGerarEValidarToken() {
        String token = jwtService.gerarToken("admin", "ADMIN");

        assertNotNull(token);
        assertTrue(jwtService.isTokenValido(token));
    }

    @Test
    @Story("Extrair username do token")
    void deveExtrairUsername() {
        String token = jwtService.gerarToken("joao", "USER");
        assertEquals("joao", jwtService.extrairUsername(token));
    }

    @Test
    @Story("Extrair role do token")
    void deveExtrairRole() {
        String token = jwtService.gerarToken("joao", "GESTOR");
        assertEquals("GESTOR", jwtService.extrairRole(token));
    }

    @Test
    @Story("Token inválido retorna false")
    void tokenInvalidoRetornaFalse() {
        assertFalse(jwtService.isTokenValido("token.invalido.aqui"));
    }

    @Test
    @Story("Token nulo retorna false")
    void tokenNuloRetornaFalse() {
        assertFalse(jwtService.isTokenValido(null));
    }

    @Test
    @Story("Chave curta deve ser expandida para 256 bits")
    void chavesCurtas_devemSerPaddedPara256bits() {
        // chave com menos de 32 bytes, deve fazer padding
        var svc = new JwtService("curta", 3600000);
        String token = svc.gerarToken("teste", "USER");
        assertTrue(svc.isTokenValido(token));
    }
}
