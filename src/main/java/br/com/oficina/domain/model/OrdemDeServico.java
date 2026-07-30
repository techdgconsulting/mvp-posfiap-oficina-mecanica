package br.com.oficina.domain.model;

import br.com.oficina.domain.valueobject.StatusOS;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrdemDeServico {

    private Long id;
    private String numero;
    @Builder.Default
    private StatusOS status = StatusOS.RECEBIDA;
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private LocalDateTime dataFinalizacao;
    private String atendenteNome;
    private Long clienteId;
    private Cliente cliente;
    private Long veiculoId;
    private Veiculo veiculo;
    @Builder.Default
    private List<ItemOS> itens = new ArrayList<>();

    public static OrdemDeServico criar(Cliente cliente, Veiculo veiculo) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente e obrigatorio para criar uma OS");
        }
        if (veiculo == null) {
            throw new IllegalArgumentException("Veiculo e obrigatorio para criar uma OS");
        }
        return OrdemDeServico.builder()
                .cliente(cliente)
                .clienteId(cliente.getId())
                .veiculo(veiculo)
                .veiculoId(veiculo.getId())
                .build();
    }

    public void atribuirId(Long id) {
        this.id = id;
        for (var item : itens) {
            item.associarOrdem(id);
        }
    }

    public void atribuirNumero(String numero) {
        this.numero = numero;
    }

    public void atribuirAtendente(String atendenteNome) {
        this.atendenteNome = atendenteNome;
    }

    public void adicionarItem(ItemOS item) {
        if (item == null) {
            throw new IllegalArgumentException("Item e obrigatorio");
        }
        if (this.id != null) {
            item.associarOrdem(this.id);
        }
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
            throw new IllegalStateException("Nao e possivel cancelar uma OS finalizada ou entregue");
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
