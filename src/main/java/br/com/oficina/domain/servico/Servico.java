package br.com.oficina.domain.servico;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "servicos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Column(name = "valor_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "tempo_estimado_minutos")
    private Integer tempoEstimadoMinutos;

    public Servico(String nome, String descricao, BigDecimal valorUnitario, Integer tempoEstimadoMinutos) {
        this.nome = nome;
        this.descricao = descricao;
        this.valorUnitario = valorUnitario;
        this.tempoEstimadoMinutos = tempoEstimadoMinutos;
    }

    public void atualizar(String nome, String descricao, BigDecimal valorUnitario, Integer tempoEstimadoMinutos) {
        this.nome = nome;
        this.descricao = descricao;
        this.valorUnitario = valorUnitario;
        this.tempoEstimadoMinutos = tempoEstimadoMinutos;
    }
}
