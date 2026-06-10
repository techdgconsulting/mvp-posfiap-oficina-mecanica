package br.com.oficina.domain.financeiro;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ordem_servico_id", nullable = false)
    private Long ordemDeServicoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusPagamento status = StatusPagamento.PENDENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pagamento")
    private MetodoPagamento metodo;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    /** Id da transação retornado pelo gateway de pagamento externo. */
    @Column(name = "transaction_id", length = 60)
    private String transactionId;

    /** Mensagem retornada pelo gateway (motivo da aprovação/recusa). */
    @Column(name = "gateway_mensagem", length = 255)
    private String gatewayMensagem;

    public void aprovar() {
        aprovar(null, null);
    }

    public void aprovar(String transactionId, String gatewayMensagem) {
        if (this.status != StatusPagamento.PENDENTE) {
            throw new IllegalStateException("Não é possível aprovar pagamento com status " + status);
        }
        this.status = StatusPagamento.APROVADO;
        this.dataPagamento = LocalDateTime.now();
        this.transactionId = transactionId;
        this.gatewayMensagem = gatewayMensagem;
    }

    public void recusar() {
        recusar(null, null);
    }

    public void recusar(String transactionId, String gatewayMensagem) {
        if (this.status != StatusPagamento.PENDENTE) {
            throw new IllegalStateException("Pagamento já processado, não pode ser recusado");
        }
        this.status = StatusPagamento.RECUSADO;
        this.transactionId = transactionId;
        this.gatewayMensagem = gatewayMensagem;
    }
}
