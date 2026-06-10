package br.com.oficina.domain.entrega;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "entregas")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ordem_servico_id", nullable = false)
    private Long ordemDeServicoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusEntrega status = StatusEntrega.AGUARDANDO_LIBERACAO;

    @Column(name = "data_entrega")
    private LocalDateTime dataEntrega;

    private String observacoes;

    public static Entrega criar(Long ordemDeServicoId) {
        return Entrega.builder()
                .ordemDeServicoId(ordemDeServicoId)
                .build();
    }

    public void liberarVeiculo() {
        if (this.status != StatusEntrega.AGUARDANDO_LIBERACAO) {
            throw new IllegalStateException("Veículo só pode ser liberado quando está AGUARDANDO_LIBERACAO. Status atual: " + status);
        }
        this.status = StatusEntrega.VEICULO_LIBERADO;
    }

    public void entregarVeiculo() {
        if (this.status != StatusEntrega.VEICULO_LIBERADO) {
            throw new IllegalStateException("Veículo só pode ser entregue quando está LIBERADO. Status atual: " + status);
        }
        this.status = StatusEntrega.VEICULO_ENTREGUE;
        this.dataEntrega = LocalDateTime.now();
    }

    public void registrarEmPatio() {
        if (this.status != StatusEntrega.VEICULO_LIBERADO) {
            throw new IllegalStateException("Veículo só pode ir para pátio quando está LIBERADO. Status atual: " + status);
        }
        this.status = StatusEntrega.VEICULO_EM_PATIO;
    }

    public void retirarDoPatio() {
        if (this.status != StatusEntrega.VEICULO_EM_PATIO) {
            throw new IllegalStateException("Veículo precisa estar EM_PATIO para ser retirado. Status atual: " + status);
        }
        this.status = StatusEntrega.VEICULO_ENTREGUE;
        this.dataEntrega = LocalDateTime.now();
    }
}
