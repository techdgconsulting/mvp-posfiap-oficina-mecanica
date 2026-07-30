package br.com.oficina.application.command;

public record AtualizarClienteCommand(
    Long id,
    String nome,
    String telefone,
    String email,
    String atendenteNome
) {}
