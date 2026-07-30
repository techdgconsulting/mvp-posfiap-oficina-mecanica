package br.com.oficina.application.query;

public record ClienteResult(
    Long id,
    String documento,
    String tipoDocumento,
    String nome,
    String telefone,
    String email,
    String cep,
    String logradouro,
    String bairro,
    String cidade,
    String uf
) {}
