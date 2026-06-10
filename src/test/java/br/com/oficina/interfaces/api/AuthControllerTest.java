package br.com.oficina.interfaces.api;

import br.com.oficina.infrastructure.security.JwtService;
import br.com.oficina.infrastructure.security.Usuario;
import br.com.oficina.infrastructure.security.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Epic("Segurança e Autenticação")
@Feature("API REST Autenticação")
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    @MockBean private AuthenticationManager authManager;
    @MockBean private JwtService jwtService;
    @MockBean private UsuarioRepository usuarioRepo;
    @MockBean private PasswordEncoder passwordEncoder;
    @MockBean private UserDetailsService userDetailsService;

    @Test
    @Story("Efetuar login com credenciais válidas")
    void deveLogar() throws Exception {
        var usuario = new Usuario("admin", "encoded", "ADMIN");
        when(authManager.authenticate(any())).thenReturn(
            new UsernamePasswordAuthenticationToken("admin", "senha123"));
        when(usuarioRepo.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(jwtService.gerarToken("admin", "ADMIN")).thenReturn("jwt-token-aqui");

        mockMvc.perform(post("/api/auth/login").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("username", "admin", "password", "senha123"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("jwt-token-aqui"))
            .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    @Story("Registrar novo usuário")
    void deveRegistrar() throws Exception {
        when(usuarioRepo.existsByUsername("novo")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");

        mockMvc.perform(post("/api/auth/registro").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("username", "novo", "password", "pass123", "role", "ATENDENTE"))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("novo"));
    }

    @Test
    @Story("Rejeitar registro de usuário duplicado")
    void registroDuplicado_retornaBadRequest() throws Exception {
        when(usuarioRepo.existsByUsername("admin")).thenReturn(true);

        mockMvc.perform(post("/api/auth/registro").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("username", "admin", "password", "123456", "role", "ATENDENTE"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.erro").value("Username já existe"));
    }

    @Test
    @Story("Rejeitar login sem campos obrigatórios")
    void loginSemCampos_retorna400() throws Exception {
        mockMvc.perform(post("/api/auth/login").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Story("Rejeitar login com credenciais inválidas")
    void loginComCredenciaisInvalidas_retorna401() throws Exception {
        when(authManager.authenticate(any())).thenThrow(new BadCredentialsException("Credenciais inválidas"));

        mockMvc.perform(post("/api/auth/login").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("username", "admin", "password", "errada"))))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Story("Registrar usuário com role explícita")
    void deveRegistrarComRoleExplicita() throws Exception {
        when(usuarioRepo.existsByUsername("user2")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");

        mockMvc.perform(post("/api/auth/registro").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "username", "user2", "password", "pass123", "role", "GESTOR"))))
            .andExpect(status().isCreated());
    }

    @Test
    @Story("Rejeitar registro com role inválida")
    void registrarComRoleInvalida_retorna400() throws Exception {
        when(usuarioRepo.existsByUsername("user3")).thenReturn(false);

        mockMvc.perform(post("/api/auth/registro").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "username", "user3", "password", "pass123", "role", "ADMIN"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.erro").value("Perfil inv\u00e1lido. Use: ATENDENTE, MECANICO ou GESTOR"));
    }
}
