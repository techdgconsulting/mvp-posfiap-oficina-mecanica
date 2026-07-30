package br.com.oficina.adapters.in.web.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CriarOrdemServicoCompletaRequest(
    @NotNull(message = "Cliente e obrigatorio")
    @Valid
    ClienteCompletoRequest cliente,

    @NotNull(message = "Veiculo e obrigatorio")
    @Valid
    VeiculoCompletoRequest veiculo,

    @Valid
    List<ServicoOSRequest> servicos,

    @Valid
    List<PecaOSRequest> pecas
) {

    public record ClienteCompletoRequest(
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
        String cep,

        String logradouro,
        String bairro,
        String cidade,
        String uf
    ) {}

    public record VeiculoCompletoRequest(
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
        int ano
    ) {}

    public record ServicoOSRequest(
        @NotNull(message = "ID do servico e obrigatorio")
        Long servicoId,

        @Positive(message = "Quantidade deve ser positiva")
        @Min(value = 1, message = "Quantidade minima e 1")
        @Max(value = 9999, message = "Quantidade maxima e 9999")
        int quantidade
    ) {}

    public record PecaOSRequest(
        @NotNull(message = "ID da peca e obrigatorio")
        Long pecaId,

        @Positive(message = "Quantidade deve ser positiva")
        @Min(value = 1, message = "Quantidade minima e 1")
        @Max(value = 9999, message = "Quantidade maxima e 9999")
        int quantidade
    ) {}
}
