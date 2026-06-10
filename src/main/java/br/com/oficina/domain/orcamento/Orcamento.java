package br.com.oficina.domain.orcamento;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orcamentos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ordem_servico_id", nullable = false)
    private Long ordemDeServicoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusOrcamento status = StatusOrcamento.PENDENTE;

    @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "data_criacao", nullable = false)
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_validade")
    private LocalDateTime dataValidade;

    @Column(name = "data_aprovacao")
    private LocalDateTime dataAprovacao;

    public static Orcamento gerar(Long ordemDeServicoId, BigDecimal valorTotal) {
        if (valorTotal == null || valorTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do orçamento deve ser maior que zero");
        }
        return Orcamento.builder()
                .ordemDeServicoId(ordemDeServicoId)
                .valorTotal(valorTotal)
                .dataValidade(LocalDateTime.now().plusDays(7))
                .build();
    }

    public void enviar() {
        if (status != StatusOrcamento.PENDENTE) {
            throw new IllegalStateException("Orçamento só pode ser enviado quando PENDENTE. Status atual: " + status);
        }
        this.status = StatusOrcamento.ENVIADO;
    }

    public void aprovar() {
        if (status != StatusOrcamento.ENVIADO) {
            throw new IllegalStateException("Orçamento só pode ser aprovado quando ENVIADO. Status atual: " + status);
        }
        if (estaExpirado()) {
            throw new IllegalStateException("Orçamento expirado, não pode ser aprovado");
        }
        this.status = StatusOrcamento.APROVADO;
        this.dataAprovacao = LocalDateTime.now();
    }

    public void rejeitar() {
        if (status != StatusOrcamento.ENVIADO) {
            throw new IllegalStateException("Orçamento só pode ser rejeitado quando ENVIADO. Status atual: " + status);
        }
        this.status = StatusOrcamento.REJEITADO;
    }

    public boolean estaExpirado() {
        return dataValidade != null && LocalDateTime.now().isAfter(dataValidade);
    }
}
