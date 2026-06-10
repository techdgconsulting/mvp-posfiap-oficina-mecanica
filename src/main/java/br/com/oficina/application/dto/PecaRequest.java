package br.com.oficina.application.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PecaRequest(
    @NotBlank(message = "Nome da peça é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    String nome,

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    String descricao,

    @NotNull
    @PositiveOrZero
    @Max(value = 99999, message = "Quantidade máxima é 99999")
    Integer quantidadeEstoque,

    @NotNull
    @Positive(message = "Valor unitário deve ser positivo")
    @DecimalMin(value = "0.01", message = "Valor mínimo é R$ 0,01")
    @DecimalMax(value = "999999.99", message = "Valor máximo é R$ 999.999,99")
    BigDecimal valorUnitario,

    @PositiveOrZero(message = "Estoque mínimo deve ser zero ou positivo")
    @Max(value = 99999, message = "Estoque mínimo máximo é 99999")
    Integer estoqueMinimo
) {}
