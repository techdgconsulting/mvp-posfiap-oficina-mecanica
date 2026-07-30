package br.com.oficina.application.query;

import java.time.LocalDateTime;

public record MetricasOSResult(
    String numero,
    String status,
    LocalDateTime dataCriacao,
    LocalDateTime dataFinalizacao,
    String tempoExecucao,
    LocalDateTime dataEntrega,
    String tempoAtendimento
) {}
