package br.com.oficina.domain.ordemservico;

import br.com.oficina.domain.atendimento.cliente.Cliente;
import br.com.oficina.domain.atendimento.veiculo.Veiculo;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate root principal do sistema.
 * Representa o ciclo completo de atendimento de um veículo na oficina.
 *
 * Fluxo de status:
 * RECEBIDA -> EM_DIAGNOSTICO -> AGUARDANDO_APROVACAO -> EM_EXECUCAO -> FINALIZADA -> ENTREGUE
 */
@Entity
@Table(name = "ordens_servico")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrdemDeServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero", unique = true)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusOS status = StatusOS.RECEBIDA;

    @Column(name = "data_criacao", nullable = false)
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_finalizacao")
    private LocalDateTime dataFinalizacao;

    @Column(name = "atendente_nome", length = 150)
    private String atendenteNome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @OneToMany(mappedBy = "ordemDeServico", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemOS> itens = new ArrayList<>();

    public static OrdemDeServico criar(Cliente cliente, Veiculo veiculo) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente é obrigatório para criar uma OS");
        }
        if (veiculo == null) {
            throw new IllegalArgumentException("Veículo é obrigatório para criar uma OS");
        }
        return OrdemDeServico.builder()
                .cliente(cliente)
                .veiculo(veiculo)
                .build();
    }

    public void atribuirNumero(String numero) {
        this.numero = numero;
    }

    public void atribuirAtendente(String atendenteNome) {
        this.atendenteNome = atendenteNome;
    }

    public void adicionarItem(ItemOS item) {
        item.associarOrdem(this);
        this.itens.add(item);
    }

    public BigDecimal calcularValorTotal() {
        return itens.stream()
                .map(ItemOS::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void avancarParaDiagnostico() {
        validarTransicao(StatusOS.RECEBIDA, StatusOS.EM_DIAGNOSTICO);
        this.status = StatusOS.EM_DIAGNOSTICO;
    }

    // pode vir de EM_DIAGNOSTICO (fluxo normal) ou EM_EXECUCAO (quando mecânico acha problema novo)
    public void aguardarAprovacao() {
        if (status != StatusOS.EM_DIAGNOSTICO && status != StatusOS.EM_EXECUCAO) {
            throw new IllegalStateException(
                "Transição inválida para AGUARDANDO_APROVACAO. Status atual: " + status
            );
        }
        this.status = StatusOS.AGUARDANDO_APROVACAO;
    }

    public void aprovarEIniciarExecucao() {
        validarTransicao(StatusOS.AGUARDANDO_APROVACAO, StatusOS.EM_EXECUCAO);
        this.status = StatusOS.EM_EXECUCAO;
    }

    public void finalizar() {
        validarTransicao(StatusOS.EM_EXECUCAO, StatusOS.FINALIZADA);
        this.status = StatusOS.FINALIZADA;
        this.dataFinalizacao = LocalDateTime.now();
    }

    public void aguardarRetirada() {
        validarTransicao(StatusOS.FINALIZADA, StatusOS.AGUARDANDO_RETIRADA);
        this.status = StatusOS.AGUARDANDO_RETIRADA;
    }

    public void entregar() {
        validarTransicao(StatusOS.AGUARDANDO_RETIRADA, StatusOS.ENTREGUE);
        this.status = StatusOS.ENTREGUE;
    }

    public void cancelar() {
        if (status == StatusOS.FINALIZADA || status == StatusOS.ENTREGUE) {
            throw new IllegalStateException("Não é possível cancelar uma OS finalizada ou entregue");
        }
        this.status = StatusOS.CANCELADA;
    }

    private void validarTransicao(StatusOS esperado, StatusOS novo) {
        if (this.status != esperado) {
            throw new IllegalStateException(
                String.format("Transição inválida: %s -> %s. Status atual: %s", esperado, novo, this.status)
            );
        }
    }
}
