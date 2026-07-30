package br.com.oficina.infrastructure.security;

import br.com.oficina.application.port.out.UsuarioRepositoryPort;
import br.com.oficina.domain.model.Usuario;
import br.com.oficina.domain.valueobject.PerfilUsuario;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Epic("Seguranca e Autenticacao")
@Feature("UserDetails Service")
class CustomUserDetailsServiceTest {

    @Mock private UsuarioRepositoryPort usuarioRepositoryPort;
    @InjectMocks private CustomUserDetailsService service;

    @Test
    @Story("Carregar usuario existente")
    void deveCarregarUsuario() {
        var usuario = new Usuario(1L, "admin", "senha123", PerfilUsuario.GESTOR);

        when(usuarioRepositoryPort.buscarPorUsername("admin")).thenReturn(Optional.of(usuario));

        var details = service.loadUserByUsername("admin");
        assertEquals("admin", details.getUsername());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_GESTOR")));
    }

    @Test
    @Story("Lancar excecao se usuario nao encontrado")
    void deveLancarSeNaoEncontrar() {
        when(usuarioRepositoryPort.buscarPorUsername("xxx")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("xxx"));
    }
}
