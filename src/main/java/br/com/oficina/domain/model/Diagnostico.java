package br.com.oficina.domain.model;

import br.com.oficina.domain.valueobject.StatusDiagnostico;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diagnostico {

    private Long id;
    private String descricaoProblema;
    private LocalDateTime dataDiagnostico;
    @Builder.Default
    private StatusDiagnostico status = StatusDiagnostico.PENDENTE;

    public void iniciar() {
        if (status != StatusDiagnostico.PENDENTE) {
            throw new IllegalStateException("Diagnostico ja foi iniciado");
        }
        this.status = StatusDiagnostico.EM_ANDAMENTO;
        this.dataDiagnostico = LocalDateTime.now();
    }

    public void identificarProblema(String descricao) {
        if (status != StatusDiagnostico.EM_ANDAMENTO) {
            throw new IllegalStateException("Diagnostico precisa estar EM_ANDAMENTO para identificar problemas");
        }
        this.descricaoProblema = descricao;
    }

    public void concluir() {
        if (status != StatusDiagnostico.EM_ANDAMENTO) {
            throw new IllegalStateException("Diagnostico precisa estar EM_ANDAMENTO para ser concluido");
        }
        this.status = StatusDiagnostico.CONCLUIDO;
    }
}
