package br.com.oficina.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank
    @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
    String username,


    @NotBlank
    @Size(min = 6, max = 100, message = "Password deve ter entre 6 e 100 caracteres")
    String password
) {}
