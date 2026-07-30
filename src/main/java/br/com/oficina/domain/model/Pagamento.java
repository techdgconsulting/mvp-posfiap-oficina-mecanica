package br.com.oficina.domain.model;

import br.com.oficina.domain.valueobject.MetodoPagamento;
import br.com.oficina.domain.valueobject.StatusPagamento;
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
public class Pagamento {

    private Long id;
    private Long ordemDeServicoId;
    @Builder.Default
    private StatusPagamento status = StatusPagamento.PENDENTE;
    private MetodoPagamento metodo;
    private BigDecimal valor;
    private LocalDateTime dataPagamento;
    private String transactionId;
    private String gatewayMensagem;

    public static Pagamento criar(Long ordemDeServicoId, BigDecimal valor, MetodoPagamento metodo) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do pagamento deve ser maior que zero");
        }
        if (metodo == null) {
            throw new IllegalArgumentException("Metodo de pagamento e obrigatorio");
        }
        return Pagamento.builder()
                .ordemDeServicoId(ordemDeServicoId)
                .valor(valor)
                .metodo(metodo)
                .build();
    }

    public void aprovar() {
        aprovar(null, null);
    }

    public void aprovar(String transactionId, String gatewayMensagem) {
        if (this.status != StatusPagamento.PENDENTE) {
            throw new IllegalStateException("Nao e possivel aprovar pagamento com status " + status);
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
            throw new IllegalStateException("Pagamento ja processado, nao pode ser recusado");
        }
        this.status = StatusPagamento.RECUSADO;
        this.transactionId = transactionId;
        this.gatewayMensagem = gatewayMensagem;
    }
}
