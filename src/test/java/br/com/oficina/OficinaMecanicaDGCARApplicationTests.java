package br.com.oficina;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

@SpringBootTest
@ActiveProfiles("test")
@Epic("Aplicação")
@Feature("Context Load")
class OficinaMecanicaDGCARApplicationTests {

    @Test
    @Story("Contexto Spring sobe sem erros")
    void contextLoads() {
        // verifica se o contexto sobe sem erro
    }
}
