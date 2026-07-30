package br.com.oficina.adapters.in.web.controller;

import br.com.oficina.adapters.in.web.mapper.AuthWebMapper;
import br.com.oficina.application.exception.CredenciaisInvalidasException;
import br.com.oficina.application.port.in.AutenticarLoginInputPort;
import br.com.oficina.application.port.in.RegistrarUsuarioInputPort;
import br.com.oficina.application.port.out.TokenProviderPort;
import br.com.oficina.application.query.LoginResult;
import br.com.oficina.application.query.UsuarioResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({AuthWebMapper.class, GlobalExceptionHandler.class})
@Epic("Seguranca e Autenticacao")
@Feature("API REST Autenticacao")
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    @MockitoBean private AutenticarLoginInputPort autenticarLoginInputPort;
    @MockitoBean private RegistrarUsuarioInputPort registrarUsuarioInputPort;
    @MockitoBean private TokenProviderPort tokenProviderPort;

    @Test
    @Story("Efetuar login com credenciais validas")
    void deveLogar() throws Exception {
        when(autenticarLoginInputPort.execute(any()))
                .thenReturn(new LoginResult("jwt-token-aqui", "admin", "GESTOR"));

        mockMvc.perform(post("/api/auth/login").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("username", "admin", "password", "senha123"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("jwt-token-aqui"))
            .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    @Story("Registrar novo usuario")
    void deveRegistrar() throws Exception {
        when(registrarUsuarioInputPort.execute(any()))
                .thenReturn(new UsuarioResult(1L, "novo", "ATENDENTE"));

        mockMvc.perform(post("/api/auth/registro").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("username", "novo", "password", "pass123", "role", "ATENDENTE"))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("novo"));
    }

    @Test
    @Story("Rejeitar registro de usuario duplicado")
    void registroDuplicado_retornaBadRequest() throws Exception {
        when(registrarUsuarioInputPort.execute(any()))
                .thenThrow(new IllegalArgumentException("Username já existe"));

        mockMvc.perform(post("/api/auth/registro").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("username", "admin", "password", "123456", "role", "ATENDENTE"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.erro").value("Username já existe"));
    }

    @Test
    @Story("Rejeitar login sem campos obrigatorios")
    void loginSemCampos_retorna400() throws Exception {
        mockMvc.perform(post("/api/auth/login").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Story("Rejeitar login com credenciais invalidas")
    void loginComCredenciaisInvalidas_retorna401() throws Exception {
        when(autenticarLoginInputPort.execute(any())).thenThrow(new CredenciaisInvalidasException());

        mockMvc.perform(post("/api/auth/login").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("username", "admin", "password", "errada"))))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Story("Registrar usuario com role explicita")
    void deveRegistrarComRoleExplicita() throws Exception {
        when(registrarUsuarioInputPort.execute(any()))
                .thenReturn(new UsuarioResult(2L, "user2", "GESTOR"));

        mockMvc.perform(post("/api/auth/registro").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "username", "user2", "password", "pass123", "role", "GESTOR"))))
            .andExpect(status().isCreated());
    }

    @Test
    @Story("Rejeitar registro com role invalida")
    void registrarComRoleInvalida_retorna400() throws Exception {
        when(registrarUsuarioInputPort.execute(any()))
                .thenThrow(new IllegalArgumentException("Perfil inválido. Use: ATENDENTE, MECANICO ou GESTOR"));

        mockMvc.perform(post("/api/auth/registro").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "username", "user3", "password", "pass123", "role", "ADMIN"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.erro").value("Perfil inv\u00e1lido. Use: ATENDENTE, MECANICO ou GESTOR"));
    }
}
