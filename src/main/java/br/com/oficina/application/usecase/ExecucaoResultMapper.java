package br.com.oficina.application.usecase;

import br.com.oficina.application.query.ExecucaoResult;
import br.com.oficina.domain.model.Execucao;

final class ExecucaoResultMapper {

    private ExecucaoResultMapper() {
    }

    static ExecucaoResult toResult(Execucao execucao) {
        var diagnostico = execucao.getDiagnostico();
        var periodo = execucao.getPeriodoExecucao();
        return new ExecucaoResult(
                execucao.getId(),
                execucao.getOrdemDeServicoId(),
                execucao.getStatus().name(),
                diagnostico != null ? diagnostico.getId() : null,
                diagnostico != null ? diagnostico.getDescricaoProblema() : null,
                diagnostico != null ? diagnostico.getDataDiagnostico() : null,
                diagnostico != null ? diagnostico.getStatus().name() : null,
                periodo != null ? periodo.getInicio() : null,
                periodo != null ? periodo.getFim() : null,
                execucao.calcularTempoExecucao().toMinutes(),
                execucao.getMecanicoNome());
    }
}
