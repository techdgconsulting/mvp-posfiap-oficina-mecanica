package br.com.oficina.domain.encerramento;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "encerramentos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Encerramento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ordem_servico_id", nullable = false)
    private Long ordemDeServicoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusEncerramento status = StatusEncerramento.PENDENTE;

    @Column(name = "data_encerramento")
    private LocalDateTime dataEncerramento;

    public static Encerramento criar(Long ordemDeServicoId) {
        return Encerramento.builder()
                .ordemDeServicoId(ordemDeServicoId)
                .build();
    }

    public void encerrar() {
        if (this.status != StatusEncerramento.PENDENTE) {
            throw new IllegalStateException("OS já foi encerrada");
        }
        this.status = StatusEncerramento.ENCERRADA;
        this.dataEncerramento = LocalDateTime.now();
    }
}
