package br.com.oficina.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
    @NotBlank(message = "Documento é obrigatório")
    @Size(min = 11, max = 18, message = "Documento deve ter entre 11 e 18 caracteres")
    String documento,

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    String nome,

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    String telefone,

    @Email(message = "Email inválido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    String email,

    @Size(min = 8, max = 9, message = "CEP deve ter entre 8 e 9 caracteres")
    String cep
) {}
