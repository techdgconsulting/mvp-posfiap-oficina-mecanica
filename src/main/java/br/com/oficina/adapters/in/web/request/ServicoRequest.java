package br.com.oficina.adapters.in.web.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ServicoRequest(
    @NotBlank(message = "Nome do servico e obrigatorio")
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
    @Positive(message = "Valor deve ser positivo")
    @DecimalMin(value = "0.01", message = "Valor minimo e R$ 0,01")
    @DecimalMax(value = "999999.99", message = "Valor maximo e R$ 999.999,99")
    BigDecimal valorUnitario,

    @Min(value = 1, message = "Tempo estimado minimo e 1 minuto")
    @Max(value = 14400, message = "Tempo estimado maximo e 14400 minutos (10 dias)")
    Integer tempoEstimadoMinutos
) {}
