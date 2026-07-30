package br.com.oficina.application.command;

public record NotificarStatusOrdemServicoCommand(
        Long ordemServicoId,
        String numeroOS,
        String status,
        String clienteNome,
        String clienteEmail
) {}
