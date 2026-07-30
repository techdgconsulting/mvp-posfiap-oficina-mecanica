package br.com.oficina.domain.model;

import br.com.oficina.domain.valueobject.StatusEntrega;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Entrega {

    private Long id;
    private Long ordemDeServicoId;
    @Builder.Default
    private StatusEntrega status = StatusEntrega.AGUARDANDO_LIBERACAO;
    private LocalDateTime dataEntrega;
    private String observacoes;

    public static Entrega criar(Long ordemDeServicoId) {
        return Entrega.builder()
                .ordemDeServicoId(ordemDeServicoId)
                .build();
    }

    public void liberarVeiculo() {
        if (this.status != StatusEntrega.AGUARDANDO_LIBERACAO) {
            throw new IllegalStateException(
                    "Veiculo so pode ser liberado quando esta AGUARDANDO_LIBERACAO. Status atual: " + status);
        }
        this.status = StatusEntrega.VEICULO_LIBERADO;
    }

    public void entregarVeiculo() {
        if (this.status != StatusEntrega.VEICULO_LIBERADO) {
            throw new IllegalStateException(
                    "Veiculo so pode ser entregue quando esta LIBERADO. Status atual: " + status);
        }
        this.status = StatusEntrega.VEICULO_ENTREGUE;
        this.dataEntrega = LocalDateTime.now();
    }

    public void registrarEmPatio() {
        if (this.status != StatusEntrega.VEICULO_LIBERADO) {
            throw new IllegalStateException(
                    "Veiculo so pode ir para patio quando esta LIBERADO. Status atual: " + status);
        }
        this.status = StatusEntrega.VEICULO_EM_PATIO;
    }

    public void retirarDoPatio() {
        if (this.status != StatusEntrega.VEICULO_EM_PATIO) {
            throw new IllegalStateException(
                    "Veiculo precisa estar EM_PATIO para ser retirado. Status atual: " + status);
        }
        this.status = StatusEntrega.VEICULO_ENTREGUE;
        this.dataEntrega = LocalDateTime.now();
    }
}
