package br.com.oficina.adapters.in.web.controller;

import br.com.oficina.adapters.in.web.mapper.AuthWebMapper;
import br.com.oficina.adapters.in.web.request.LoginRequest;
import br.com.oficina.adapters.in.web.request.RegistroRequest;
import br.com.oficina.adapters.in.web.response.LoginResponse;
import br.com.oficina.adapters.in.web.response.UsuarioResponse;
import br.com.oficina.application.port.in.AutenticarLoginInputPort;
import br.com.oficina.application.port.in.RegistrarUsuarioInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacao")
public class AuthController {

    private final AutenticarLoginInputPort autenticarLoginInputPort;
    private final RegistrarUsuarioInputPort registrarUsuarioInputPort;
    private final AuthWebMapper mapper;

    @PostMapping("/login")
    @Operation(summary = "Fazer login e obter token JWT")
    @SecurityRequirements
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(mapper.toResponse(autenticarLoginInputPort.execute(mapper.toCommand(request))));
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar novo usuario")
    @SecurityRequirements
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(registrarUsuarioInputPort.execute(mapper.toCommand(request))));
    }
}
