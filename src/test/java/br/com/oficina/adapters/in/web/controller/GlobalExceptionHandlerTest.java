package br.com.oficina.adapters.in.web.controller;

import br.com.oficina.application.port.out.TokenProviderPort;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.qameta.allure.Story;

@WebMvcTest(
    controllers = TestExceptionController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
    }
)
@Import(GlobalExceptionHandler.class)
@Tag("TratamentoDeErros")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private TokenProviderPort tokenProviderPort;
    @MockitoBean private UserDetailsService userDetailsService;

    @Test
    @Story("RecursoNaoEncontradoException retorna 404")
    void handleNotFound_retorna404() throws Exception {
        mockMvc.perform(get("/test-exceptions/not-found"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.erro").value("Recurso nao encontrado"))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @Story("NegocioException retorna 422")
    void handleNegocio_retorna422() throws Exception {
        mockMvc.perform(get("/test-exceptions/negocio"))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.status").value(422))
            .andExpect(jsonPath("$.erro").value("Regra de negocio violada"));
    }

    @Test
    @Story("DocumentoInvalidoException retorna 422")
    void handleDocumentoInvalido_retorna422() throws Exception {
        mockMvc.perform(get("/test-exceptions/documento-invalido"))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.status").value(422))
            .andExpect(jsonPath("$.erro").value("CPF invalido"));
    }

    @Test
    @Story("BadCredentialsException retorna 401")
    void handleBadCredentials_retorna401() throws Exception {
        mockMvc.perform(get("/test-exceptions/bad-credentials"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.erro").value("Credenciais inv\u00e1lidas"));
    }

    @Test
    @Story("IllegalArgumentException retorna 400")
    void handleIllegalArg_retorna400() throws Exception {
        mockMvc.perform(get("/test-exceptions/illegal-arg"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.erro").value("argumento invalido"));
    }

    @Test
    @Story("IllegalStateException retorna 409")
    void handleIllegalState_retorna409() throws Exception {
        mockMvc.perform(get("/test-exceptions/illegal-state"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.erro").value("estado invalido"));
    }

    @Test
    @Story("JSON malformado retorna 400")
    void handleJsonInvalido_retorna400() throws Exception {
        mockMvc.perform(post("/test-exceptions/json-invalido")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{json-invalido"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.erro").value("Corpo da requisi\u00e7\u00e3o inv\u00e1lido"));
    }

    @Test
    @Story("Metodo HTTP nao permitido retorna 405")
    void handleMethodNotSupported_retorna405() throws Exception {
        mockMvc.perform(get("/test-exceptions/json-invalido"))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(jsonPath("$.status").value(405))
            .andExpect(jsonPath("$.erro").value("Metodo HTTP nao permitido"));
    }

    @Test
    @Story("Content-Type nao suportado retorna 415")
    void handleMediaTypeNotSupported_retorna415() throws Exception {
        mockMvc.perform(post("/test-exceptions/json-invalido")
                .contentType(MediaType.TEXT_PLAIN)
                .content("texto"))
            .andExpect(status().isUnsupportedMediaType())
            .andExpect(jsonPath("$.status").value(415))
            .andExpect(jsonPath("$.erro").value("Tipo de conteudo nao suportado"));
    }

    @Test
    @Story("Formato de resposta nao aceitavel retorna 406")
    void handleMediaTypeNotAcceptable_retorna406() throws Exception {
        mockMvc.perform(get("/test-exceptions/media-type-not-acceptable"))
            .andExpect(status().isNotAcceptable())
            .andExpect(jsonPath("$.status").value(406))
            .andExpect(jsonPath("$.erro").value("Formato de resposta nao aceitavel"));
    }

    @Test
    @Story("Parametro obrigatorio ausente retorna 400")
    void handleMissingRequestParameter_retorna400() throws Exception {
        mockMvc.perform(get("/test-exceptions/missing-param"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.erro").value("Parametro obrigatorio ausente"));
    }

    @Test
    @Story("Variavel de caminho obrigatoria ausente retorna 400")
    void handleMissingPathVariable_retorna400() throws Exception {
        mockMvc.perform(get("/test-exceptions/missing-path-variable"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.erro").value("Variavel de caminho obrigatoria ausente"));
    }

    @Test
    @Story("ConstraintViolationException retorna 400")
    void handleConstraintViolation_retorna400() throws Exception {
        mockMvc.perform(get("/test-exceptions/constraint-violation"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.erro").value("Par\u00e2metros inv\u00e1lidos"));
    }

    @Test
    @Story("BindException retorna 400")
    void handleBindException_retorna400() throws Exception {
        mockMvc.perform(get("/test-exceptions/bind-exception"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.erro").value("Par\u00e2metros inv\u00e1lidos"));
    }

    @Test
    @Story("DataIntegrityViolationException retorna 400")
    void handleDataIntegrity_retorna400() throws Exception {
        mockMvc.perform(get("/test-exceptions/data-integrity"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.erro").value("Dados inv\u00e1lidos"));
    }

    @Test
    @Story("EntityNotFoundException retorna 404")
    void handleEntityNotFound_retorna404() throws Exception {
        mockMvc.perform(get("/test-exceptions/entity-not-found"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.erro").value("Registro n\u00e3o encontrado"));
    }

    @Test
    @Story("Exception generica retorna 500")
    void handleGenerico_retorna500() throws Exception {
        mockMvc.perform(get("/test-exceptions/generico"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.erro").value("Erro interno do servidor"));
    }

    @Test
    @Story("Tipo de parametro invalido retorna 400")
    void handleTypeMismatch_retorna400() throws Exception {
        mockMvc.perform(get("/test-exceptions/type-mismatch/nao-e-numero"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.erro").value(
                org.hamcrest.Matchers.containsString("id")))
            .andExpect(jsonPath("$.erro").value(
                org.hamcrest.Matchers.containsString("nao-e-numero")));
    }

    @Test
    @Story("Rota inexistente retorna 404")
    void handleNoResource_retorna404() throws Exception {
        mockMvc.perform(get("/test-exceptions/rota-que-nao-existe"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.erro").value("Recurso n\u00e3o encontrado"));
    }
}
