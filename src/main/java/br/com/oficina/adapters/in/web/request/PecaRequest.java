package br.com.oficina.adapters.in.web.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PecaRequest(
    @NotBlank(message = "Nome da peca e obrigatorio")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Pattern(
        regexp = "^(?!.*(?:<!--|-->|<\\?|\\?>|<!\\[CDATA|\\]\\]>|<[^>]+>|\\$\\{|#\\{|\\{\\{|\\}\\}|<%|%>|xsl:|javascript:)).*$",
        flags = Pattern.Flag.CASE_INSENSITIVE,
        message = "Nome contem conteudo nao permitido"
    )
    String nome,

    @Size(max = 500, message = "Descricao deve ter no maximo 500 caracteres")
    @Pattern(
        regexp = "^(?!.*(?:<!--|-->|<\\?|\\?>|<!\\[CDATA|\\]\\]>|<[^>]+>|\\$\\{|#\\{|\\{\\{|\\}\\}|<%|%>|xsl:|javascript:)).*$",
        flags = Pattern.Flag.CASE_INSENSITIVE,
        message = "Descricao contem conteudo nao permitido"
    )
    String descricao,

    @NotNull
    @PositiveOrZero
    @Max(value = 99999, message = "Quantidade maxima e 99999")
    Integer quantidadeEstoque,

    @NotNull
    @Positive(message = "Valor unitario deve ser positivo")
    @DecimalMin(value = "0.01", message = "Valor minimo e R$ 0,01")
    @DecimalMax(value = "999999.99", message = "Valor maximo e R$ 999.999,99")
    BigDecimal valorUnitario,

    @PositiveOrZero(message = "Estoque minimo deve ser zero ou positivo")
    @Max(value = 99999, message = "Estoque minimo maximo e 99999")
    Integer estoqueMinimo
) {}
