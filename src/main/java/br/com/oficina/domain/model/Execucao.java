package br.com.oficina.domain.model;

import br.com.oficina.domain.valueobject.PeriodoExecucao;
import br.com.oficina.domain.valueobject.StatusExecucao;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Execucao {

    private Long id;
    private Long ordemDeServicoId;
    @Builder.Default
    private StatusExecucao status = StatusExecucao.AGUARDANDO;
    private Diagnostico diagnostico;
    private PeriodoExecucao periodoExecucao;
    private String mecanicoNome;

    public static Execucao criar(Long ordemDeServicoId) {
        return Execucao.builder()
                .ordemDeServicoId(ordemDeServicoId)
                .diagnostico(Diagnostico.builder().build())
                .build();
    }

    public void iniciarDiagnostico(String mecanicoNome) {
        if (status != StatusExecucao.AGUARDANDO) {
            throw new IllegalStateException("Execucao precisa estar AGUARDANDO para iniciar diagnostico");
        }
        if (mecanicoNome == null || mecanicoNome.isBlank()) {
            throw new IllegalArgumentException("Mecanico responsavel e obrigatorio");
        }
        this.status = StatusExecucao.EM_DIAGNOSTICO;
        this.mecanicoNome = mecanicoNome;
        this.diagnostico.iniciar();
    }

    public void registrarDiagnostico(String descricao) {
        if (diagnostico == null) {
            diagnostico = Diagnostico.builder().build();
        }
        diagnostico.identificarProblema(descricao);
        diagnostico.concluir();
    }

    public void iniciarServico() {
        if (status != StatusExecucao.EM_DIAGNOSTICO && status != StatusExecucao.AGUARDANDO) {
            throw new IllegalStateException("Nao e possivel iniciar o servico no status atual: " + status);
        }
        this.status = StatusExecucao.EM_ANDAMENTO;
        this.periodoExecucao = new PeriodoExecucao(LocalDateTime.now());
    }

    public void finalizarServico() {
        if (status != StatusExecucao.EM_ANDAMENTO) {
            throw new IllegalStateException("Servico precisa estar EM_ANDAMENTO para ser finalizado");
        }
        this.status = StatusExecucao.SERVICO_FINALIZADO;
        if (this.periodoExecucao != null) {
            this.periodoExecucao.finalizarEm(LocalDateTime.now());
        }
    }

    public Duration calcularTempoExecucao() {
        if (periodoExecucao == null) {
            return Duration.ZERO;
        }
        return periodoExecucao.calcularDuracao();
    }

    public void atualizarMecanicoResponsavel(String mecanicoNome) {
        if (mecanicoNome == null || mecanicoNome.isBlank()) {
            throw new IllegalArgumentException("Mecanico responsavel e obrigatorio");
        }
        this.mecanicoNome = mecanicoNome;
    }
}
