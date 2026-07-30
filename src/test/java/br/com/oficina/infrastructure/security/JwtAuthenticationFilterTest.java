package br.com.oficina.infrastructure.security;

import br.com.oficina.application.port.out.TokenProviderPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

@ExtendWith(MockitoExtension.class)
@Epic("Segurança e Autenticação")
@Feature("Filtro JWT")
class JwtAuthenticationFilterTest {

    @Mock private TokenProviderPort tokenProviderPort;
    @Mock private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    @Story("Prosseguir sem header Authorization")
    void deveProsseguirSemHeaderAuthorization() throws ServletException, IOException {
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @Story("Prosseguir com header sem Bearer")
    void deveProsseguirComHeaderSemBearer() throws ServletException, IOException {
        request.addHeader("Authorization", "Basic abc123");
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @Story("Autenticar com token válido")
    void deveAutenticarComTokenValido() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer token-valido");

        when(tokenProviderPort.extrairUsername("token-valido")).thenReturn("admin");
        when(tokenProviderPort.isTokenValido("token-valido")).thenReturn(true);
        when(tokenProviderPort.extrairRole("token-valido")).thenReturn("GESTOR");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("admin", auth.getName());
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_GESTOR")));
    }

    @Test
    @Story("Montar authority correta para cada perfil")
    void deveMontarAuthorityCorretaParaCadaPerfil() throws ServletException, IOException {
        for (String role : List.of("ATENDENTE", "MECANICO", "GESTOR")) {
            SecurityContextHolder.clearContext();
            var req = new MockHttpServletRequest();
            req.addHeader("Authorization", "Bearer token-" + role);

            when(tokenProviderPort.extrairUsername("token-" + role)).thenReturn("user");
            when(tokenProviderPort.isTokenValido("token-" + role)).thenReturn(true);
            when(tokenProviderPort.extrairRole("token-" + role)).thenReturn(role);

            filter.doFilterInternal(req, response, filterChain);

            var auth = SecurityContextHolder.getContext().getAuthentication();
            assertNotNull(auth, "Esperava autenticacao para perfil " + role);
            assertTrue(
                auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + role)),
                "Esperava authority ROLE_" + role
            );
        }
    }

    @Test
    @Story("Não autenticar com token inválido")
    void naoDeveAutenticarComTokenInvalido() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer token-invalido");

        when(tokenProviderPort.extrairUsername("token-invalido")).thenReturn("admin");
        when(tokenProviderPort.isTokenValido("token-invalido")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @Story("Prosseguir com username nulo no token")
    void deveProsseguirComUsernameNulo() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer token-ruim");

        when(tokenProviderPort.extrairUsername("token-ruim")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
