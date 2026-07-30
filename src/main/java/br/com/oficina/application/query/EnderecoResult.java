package br.com.oficina.application.query;

public record EnderecoResult(
    String cep,
    String logradouro,
    String bairro,
    String cidade,
    String uf
) {}
