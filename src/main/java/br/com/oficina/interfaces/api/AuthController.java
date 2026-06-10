package br.com.oficina.interfaces.api;

import br.com.oficina.application.dto.LoginRequest;
import br.com.oficina.application.dto.LoginResponse;
import br.com.oficina.application.dto.RegistroRequest;
import br.com.oficina.infrastructure.security.JwtService;
import br.com.oficina.infrastructure.security.PerfilUsuario;
import br.com.oficina.infrastructure.security.Usuario;
import br.com.oficina.infrastructure.security.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Fazer login e obter token JWT")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        var usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(); // se autenticou, existe

        String token = jwtService.gerarToken(usuario.getUsername(), usuario.getRole());
        return ResponseEntity.ok(new LoginResponse(token, usuario.getUsername(), usuario.getRole()));
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar novo usuário")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Username já existe"));
        }

        String role = request.role().toUpperCase();
        if (!PerfilUsuario.isValido(role)) {
            return ResponseEntity.badRequest().body(Map.of(
                "erro", "Perfil inválido. Use: ATENDENTE, MECANICO ou GESTOR"));
        }

        var usuario = new Usuario(request.username(), passwordEncoder.encode(request.password()), role);
        usuarioRepository.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensagem", "Usuário criado com sucesso", "username", usuario.getUsername()));
    }
}
