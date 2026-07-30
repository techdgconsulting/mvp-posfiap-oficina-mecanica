package br.com.oficina.adapters.in.web.response;

public record VeiculoResponse(
    Long id,
    String placa,
    String marca,
    String modelo,
    int ano,
    Long clienteId,
    String clienteNome
) {}
