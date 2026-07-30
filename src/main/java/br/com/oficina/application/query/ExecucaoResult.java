package br.com.oficina.application.query;

import java.time.LocalDateTime;

public record ExecucaoResult(
    Long id,
    Long ordemDeServicoId,
    String status,
    Long diagnosticoId,
    String descricaoProblema,
    LocalDateTime dataDiagnostico,
    String statusDiagnostico,
    LocalDateTime dataInicio,
    LocalDateTime dataFim,
    long tempoExecucaoMinutos,
    String mecanicoNome
) {}
