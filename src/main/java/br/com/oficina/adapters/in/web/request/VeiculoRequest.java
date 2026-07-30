package br.com.oficina.adapters.in.web.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VeiculoRequest(
    @NotBlank(message = "Placa e obrigatoria")
    @Pattern(regexp = "^[A-Za-z]{3}[0-9][A-Za-z0-9][0-9]{2}$|^[A-Za-z]{3}-?[0-9][A-Za-z0-9][0-9]{2}$",
             message = "Placa deve estar no formato Mercosul ou antigo")
    String placa,

    @NotBlank(message = "Marca e obrigatoria")
    @Size(min = 2, max = 50, message = "Marca deve ter entre 2 e 50 caracteres")
    String marca,

    @NotBlank(message = "Modelo e obrigatorio")
    @Size(min = 2, max = 100, message = "Modelo deve ter entre 2 e 100 caracteres")
    String modelo,

    @Min(value = 1900, message = "Ano invalido")
    @Max(value = 2100, message = "Ano invalido")
    int ano,

    @NotNull(message = "ID do cliente e obrigatorio")
    Long clienteId
) {}
