package br.com.oficina.domain.execucao;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "diagnosticos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Diagnostico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descricao_problema")
    private String descricaoProblema;

    @Column(name = "data_diagnostico")
    private LocalDateTime dataDiagnostico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusDiagnostico status = StatusDiagnostico.PENDENTE;

    public void iniciar() {
        if (status != StatusDiagnostico.PENDENTE) {
            throw new IllegalStateException("Diagnóstico já foi iniciado");
        }
        this.status = StatusDiagnostico.EM_ANDAMENTO;
        this.dataDiagnostico = LocalDateTime.now();
    }

    public void identificarProblema(String descricao) {
        if (status != StatusDiagnostico.EM_ANDAMENTO) {
            throw new IllegalStateException("Diagnóstico precisa estar EM_ANDAMENTO para identificar problemas");
        }
        this.descricaoProblema = descricao;
    }

    public void concluir() {
        if (status != StatusDiagnostico.EM_ANDAMENTO) {
            throw new IllegalStateException("Diagnóstico precisa estar EM_ANDAMENTO para ser concluído");
        }
        this.status = StatusDiagnostico.CONCLUIDO;
    }
}
