package br.com.oficina.adapters.out.persistence.jpa;

import br.com.oficina.domain.valueobject.StatusEncerramento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "encerramentos")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EncerramentoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ordem_servico_id", nullable = false)
    private Long ordemDeServicoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEncerramento status;

    @Column(name = "data_encerramento")
    private LocalDateTime dataEncerramento;
}
