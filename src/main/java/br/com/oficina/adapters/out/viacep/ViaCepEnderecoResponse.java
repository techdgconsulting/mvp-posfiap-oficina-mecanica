package br.com.oficina.adapters.out.viacep;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ViaCepEnderecoResponse(
    String cep,
    String logradouro,
    String bairro,
    String localidade,
    String uf,
    Boolean erro
) {}
