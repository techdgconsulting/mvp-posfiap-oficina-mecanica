package br.com.oficina.adapters.in.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
    @NotBlank
    @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
    String username,

    @NotBlank
    @Size(min = 6, max = 100, message = "Password deve ter entre 6 e 100 caracteres")
    String password,

    @NotBlank
    @Size(min = 4, max = 20, message = "Role deve ter entre 4 e 20 caracteres")
    String role
) {}
