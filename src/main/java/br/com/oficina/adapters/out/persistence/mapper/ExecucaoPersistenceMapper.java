package br.com.oficina.adapters.out.persistence.mapper;

import br.com.oficina.adapters.out.persistence.jpa.DiagnosticoJpaEntity;
import br.com.oficina.adapters.out.persistence.jpa.ExecucaoJpaEntity;
import br.com.oficina.domain.model.Diagnostico;
import br.com.oficina.domain.model.Execucao;
import br.com.oficina.domain.valueobject.PeriodoExecucao;
import org.springframework.stereotype.Component;

@Component
public class ExecucaoPersistenceMapper {

    public ExecucaoJpaEntity toEntity(Execucao execucao) {
        var periodo = execucao.getPeriodoExecucao();
        return new ExecucaoJpaEntity(
                execucao.getId(),
                execucao.getOrdemDeServicoId(),
                execucao.getStatus(),
                toEntity(execucao.getDiagnostico()),
                periodo != null ? periodo.getInicio() : null,
                periodo != null ? periodo.getFim() : null,
                execucao.getMecanicoNome());
    }

    public Execucao toDomain(ExecucaoJpaEntity entity) {
        return Execucao.builder()
                .id(entity.getId())
                .ordemDeServicoId(entity.getOrdemDeServicoId())
                .status(entity.getStatus())
                .diagnostico(toDomain(entity.getDiagnostico()))
                .periodoExecucao(toPeriodo(entity))
                .mecanicoNome(entity.getMecanicoNome())
                .build();
    }

    private DiagnosticoJpaEntity toEntity(Diagnostico diagnostico) {
        if (diagnostico == null) {
            return null;
        }
        return new DiagnosticoJpaEntity(
                diagnostico.getId(),
                diagnostico.getDescricaoProblema(),
                diagnostico.getDataDiagnostico(),
                diagnostico.getStatus());
    }

    private Diagnostico toDomain(DiagnosticoJpaEntity entity) {
        if (entity == null) {
            return Diagnostico.builder().build();
        }
        return Diagnostico.builder()
                .id(entity.getId())
                .descricaoProblema(entity.getDescricaoProblema())
                .dataDiagnostico(entity.getDataDiagnostico())
                .status(entity.getStatus())
                .build();
    }

    private PeriodoExecucao toPeriodo(ExecucaoJpaEntity entity) {
        if (entity.getDataInicio() == null) {
            return null;
        }
        return new PeriodoExecucao(entity.getDataInicio(), entity.getDataFim());
    }
}
