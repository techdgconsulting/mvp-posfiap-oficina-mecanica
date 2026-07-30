package br.com.oficina.application.command;

public record AtualizarVeiculoCommand(
    Long id,
    String placa,
    String marca,
    String modelo,
    int ano,
    Long clienteId
) {}
