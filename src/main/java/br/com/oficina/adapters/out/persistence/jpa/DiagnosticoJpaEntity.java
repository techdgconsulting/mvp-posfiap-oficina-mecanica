package br.com.oficina.adapters.out.persistence.jpa;

import br.com.oficina.domain.valueobject.StatusDiagnostico;
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
@Table(name = "diagnosticos")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descricao_problema")
    private String descricaoProblema;

    @Column(name = "data_diagnostico")
    private LocalDateTime dataDiagnostico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDiagnostico status;
}
