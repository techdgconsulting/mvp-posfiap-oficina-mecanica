package br.com.oficina.domain.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Servico {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal valorUnitario;
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
