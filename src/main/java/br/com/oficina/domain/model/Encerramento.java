package br.com.oficina.domain.model;

import br.com.oficina.domain.valueobject.StatusEncerramento;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Encerramento {

    private Long id;
    private Long ordemDeServicoId;
    @Builder.Default
    private StatusEncerramento status = StatusEncerramento.PENDENTE;
    private LocalDateTime dataEncerramento;

    public static Encerramento criar(Long ordemDeServicoId) {
        return Encerramento.builder()
                .ordemDeServicoId(ordemDeServicoId)
                .build();
    }

    public void encerrar() {
        if (this.status != StatusEncerramento.PENDENTE) {
            throw new IllegalStateException("OS ja foi encerrada");
        }
        this.status = StatusEncerramento.ENCERRADA;
        this.dataEncerramento = LocalDateTime.now();
    }
}
