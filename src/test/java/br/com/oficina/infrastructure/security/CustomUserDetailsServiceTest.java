package br.com.oficina.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

@ExtendWith(MockitoExtension.class)
@Epic("Segurança e Autenticação")
@Feature("UserDetails Service")
class CustomUserDetailsServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @InjectMocks private CustomUserDetailsService service;

    @Test
    @Story("Carregar usuário existente")
    void deveCarregarUsuario() {
        var usuario = new Usuario();
        usuario.setUsername("admin");
        usuario.setPassword("senha123");
        usuario.setRole("ADMIN");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));

        var details = service.loadUserByUsername("admin");
        assertEquals("admin", details.getUsername());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @Story("Lançar exceção se usuário não encontrado")
    void deveLancarSeNaoEncontrar() {
        when(usuarioRepository.findByUsername("xxx")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("xxx"));
    }
}
