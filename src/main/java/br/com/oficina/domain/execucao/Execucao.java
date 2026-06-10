package br.com.oficina.domain.execucao;

import br.com.oficina.domain.execucao.vo.PeriodoExecucao;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "execucoes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Execucao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ordem_servico_id", nullable = false)
    private Long ordemDeServicoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusExecucao status = StatusExecucao.AGUARDANDO;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "diagnostico_id")
    private Diagnostico diagnostico;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "inicio", column = @Column(name = "data_inicio")),
        @AttributeOverride(name = "fim", column = @Column(name = "data_fim"))
    })
    private PeriodoExecucao periodoExecucao;

    @Column(name = "mecanico_nome", length = 150)
    private String mecanicoNome;

    public static Execucao criar(Long ordemDeServicoId) {
        return Execucao.builder()
                .ordemDeServicoId(ordemDeServicoId)
                .diagnostico(Diagnostico.builder().build())
                .build();
    }

    public void iniciarDiagnostico(String mecanicoNome) {
        if (status != StatusExecucao.AGUARDANDO) {
            throw new IllegalStateException("Execução precisa estar AGUARDANDO para iniciar diagnóstico");
        }
        this.status = StatusExecucao.EM_DIAGNOSTICO;
        this.mecanicoNome = mecanicoNome;
        this.diagnostico.iniciar();
    }

    public void iniciarServico() {
        if (status != StatusExecucao.EM_DIAGNOSTICO && status != StatusExecucao.AGUARDANDO) {
            throw new IllegalStateException("Não é possível iniciar o serviço no status atual: " + status);
        }
        this.status = StatusExecucao.EM_ANDAMENTO;
        this.periodoExecucao = new PeriodoExecucao(LocalDateTime.now());
    }

    public void finalizarServico() {
        if (status != StatusExecucao.EM_ANDAMENTO) {
            throw new IllegalStateException("Serviço precisa estar EM_ANDAMENTO para ser finalizado");
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
}
