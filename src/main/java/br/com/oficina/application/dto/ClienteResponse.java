package br.com.oficina.application.dto;

public record ClienteResponse(
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
