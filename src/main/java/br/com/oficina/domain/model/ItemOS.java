package br.com.oficina.domain.model;

import br.com.oficina.domain.valueobject.TipoItem;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ItemOS {

    private Long id;
    private TipoItem tipo;
    private String descricao;
    private int quantidade;
    private BigDecimal valorUnitario;
    private Long referenciaId;
    @Builder.Default
    private boolean estoqueReduzido = false;
    private Long ordemDeServicoId;

    public BigDecimal calcularSubtotal() {
        return valorUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public void marcarEstoqueReduzido() {
        this.estoqueReduzido = true;
    }

    public void associarOrdem(Long ordemDeServicoId) {
        this.ordemDeServicoId = ordemDeServicoId;
    }
}
