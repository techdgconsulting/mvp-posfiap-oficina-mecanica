package br.com.oficina.adapters.in.web.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ItemOSRequest(
    @NotNull(message = "Tipo e obrigatorio (SERVICO ou PECA)")
    @Size(min = 4, max = 7, message = "Tipo deve ser SERVICO ou PECA")
    String tipo,

    @NotNull(message = "ID de referencia e obrigatorio")
    Long referenciaId,

    @Positive(message = "Quantidade deve ser positiva")
    @Min(value = 1, message = "Quantidade minima e 1")
    @Max(value = 9999, message = "Quantidade maxima e 9999")
    int quantidade
) {}
