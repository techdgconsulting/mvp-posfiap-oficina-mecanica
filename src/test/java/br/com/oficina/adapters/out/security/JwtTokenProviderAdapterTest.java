package br.com.oficina.adapters.out.security;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Seguranca e Autenticacao")
@Feature("Servico JWT")
class JwtTokenProviderAdapterTest {

    private final JwtTokenProviderAdapter jwtService =
            new JwtTokenProviderAdapter("chave-secreta-para-testes-com-tamanho-suficiente", 3600000);

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
    @Story("Token invalido retorna false")
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
        var svc = new JwtTokenProviderAdapter("curta", 3600000);
        String token = svc.gerarToken("teste", "USER");
        assertTrue(svc.isTokenValido(token));
    }
}
