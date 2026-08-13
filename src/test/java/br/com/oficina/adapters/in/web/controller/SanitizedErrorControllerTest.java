package br.com.oficina.adapters.in.web.controller;

import br.com.oficina.application.port.out.TokenProviderPort;
import jakarta.servlet.RequestDispatcher;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = SanitizedErrorController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
    }
)
@Tag("TratamentoDeErros")
class SanitizedErrorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenProviderPort tokenProviderPort;

    @Test
    void erroPadraoNaoExpoeCamposSensiveis() throws Exception {
        mockMvc.perform(get("/error")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 500)
                .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/api/veiculos/10")
                .requestAttr(RequestDispatcher.ERROR_EXCEPTION, new RuntimeException("falha interna"))
                .requestAttr(RequestDispatcher.ERROR_MESSAGE, "detalhe tecnico"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.erro").value("Erro interno do servidor"))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.path").doesNotExist())
            .andExpect(jsonPath("$.error").doesNotExist())
            .andExpect(jsonPath("$.exception").doesNotExist())
            .andExpect(jsonPath("$.trace").doesNotExist())
            .andExpect(jsonPath("$.message").doesNotExist());
    }

    @Test
    void erroPadrao404NaoExpoePath() throws Exception {
        mockMvc.perform(get("/error")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 404)
                .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/api/rota-interna"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.erro").value("Recurso nao encontrado"))
            .andExpect(jsonPath("$.path").doesNotExist())
            .andExpect(jsonPath("$.error").doesNotExist());
    }
}
