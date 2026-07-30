package br.com.oficina.adapters.in.web.response;

public record DisponibilidadePecaResponse(
    Long pecaId,
    int quantidadeSolicitada,
    int quantidadeDisponivel,
    boolean disponivel
) {}
