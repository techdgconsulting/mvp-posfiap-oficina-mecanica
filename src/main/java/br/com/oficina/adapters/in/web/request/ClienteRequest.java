package br.com.oficina.adapters.in.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
    @NotBlank(message = "Documento e obrigatorio")
    @Pattern(regexp = "^[0-9./-]{11,18}$", message = "Documento deve conter apenas numeros, pontos, barra ou hifen")
    String documento,

    @NotBlank(message = "Nome e obrigatorio")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    String nome,

    @Size(max = 20, message = "Telefone deve ter no maximo 20 caracteres")
    String telefone,

    @Email(message = "Email invalido")
    @Size(max = 100, message = "Email deve ter no maximo 100 caracteres")
    String email,

    @Pattern(regexp = "^$|^[0-9-]{8,9}$", message = "CEP deve conter 8 digitos, com ou sem hifen")
    String cep
) {}
