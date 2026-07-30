package br.com.oficina.application.query;

public record VeiculoResult(
    Long id,
    String placa,
    String marca,
    String modelo,
    int ano,
    Long clienteId,
    String clienteNome
) {}
