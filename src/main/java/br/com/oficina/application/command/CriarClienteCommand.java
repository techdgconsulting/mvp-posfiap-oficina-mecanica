package br.com.oficina.application.command;

public record CriarClienteCommand(
    String documento,
    String nome,
    String telefone,
    String email,
    String cep,
    String atendenteNome
) {}
