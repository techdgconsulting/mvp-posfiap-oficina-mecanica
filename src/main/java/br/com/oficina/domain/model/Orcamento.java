package br.com.oficina.domain.model;

import br.com.oficina.domain.valueobject.StatusOrcamento;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orcamento {

    private Long id;
    private Long ordemDeServicoId;
    @Builder.Default
    private StatusOrcamento status = StatusOrcamento.PENDENTE;
    private BigDecimal valorTotal;
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private LocalDateTime dataValidade;
    private LocalDateTime dataAprovacao;

    public static Orcamento gerar(Long ordemDeServicoId, BigDecimal valorTotal) {
        if (valorTotal == null || valorTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do orcamento deve ser maior que zero");
        }
        return Orcamento.builder()
                .ordemDeServicoId(ordemDeServicoId)
                .valorTotal(valorTotal)
                .dataValidade(LocalDateTime.now().plusDays(7))
                .build();
    }

    public void enviar() {
        if (status != StatusOrcamento.PENDENTE) {
            throw new IllegalStateException("Orcamento so pode ser enviado quando PENDENTE. Status atual: " + status);
        }
        this.status = StatusOrcamento.ENVIADO;
    }

    public void aprovar() {
        if (status != StatusOrcamento.ENVIADO) {
            throw new IllegalStateException("Orcamento so pode ser aprovado quando ENVIADO. Status atual: " + status);
        }
        if (estaExpirado()) {
            throw new IllegalStateException("Orcamento expirado, nao pode ser aprovado");
        }
        this.status = StatusOrcamento.APROVADO;
        this.dataAprovacao = LocalDateTime.now();
    }

    public void rejeitar() {
        if (status != StatusOrcamento.ENVIADO) {
            throw new IllegalStateException("Orcamento so pode ser rejeitado quando ENVIADO. Status atual: " + status);
        }
        this.status = StatusOrcamento.REJEITADO;
    }

    public boolean estaExpirado() {
        return dataValidade != null && LocalDateTime.now().isAfter(dataValidade);
    }
}
