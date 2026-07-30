package br.com.oficina.application.command;

public record CriarVeiculoCommand(
    String placa,
    String marca,
    String modelo,
    int ano,
    Long clienteId
) {}
