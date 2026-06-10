package br.com.oficina.application.dto;

public record VeiculoResponse(
    Long id,
    String placa,
    String marca,
    String modelo,
    int ano,
    Long clienteId,
    String clienteNome
) {}
