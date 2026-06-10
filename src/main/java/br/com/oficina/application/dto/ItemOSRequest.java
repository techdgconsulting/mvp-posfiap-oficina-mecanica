package br.com.oficina.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// item que vai dentro da criação da OS
public record ItemOSRequest(
    @NotNull(message = "Tipo é obrigatório (SERVICO ou PECA)")
    @Size(min = 4, max = 7, message = "Tipo deve ser SERVICO ou PECA")
    String tipo,

    @NotNull(message = "ID de referência é obrigatório")
    Long referenciaId,

    @Positive(message = "Quantidade deve ser positiva")
    @Min(value = 1, message = "Quantidade mínima é 1")
    @Max(value = 9999, message = "Quantidade máxima é 9999")
    int quantidade
) {}
