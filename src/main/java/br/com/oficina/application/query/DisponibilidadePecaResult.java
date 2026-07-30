package br.com.oficina.application.query;

public record DisponibilidadePecaResult(
    Long pecaId,
    int quantidadeSolicitada,
    int quantidadeDisponivel,
    boolean disponivel
) {}
