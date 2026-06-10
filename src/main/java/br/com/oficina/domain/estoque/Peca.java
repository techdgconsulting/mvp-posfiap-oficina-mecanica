package br.com.oficina.domain.estoque;

import br.com.oficina.domain.estoque.vo.Quantidade;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "pecas")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Peca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "quantidade_estoque", nullable = false))
    @Builder.Default
    private Quantidade quantidadeEstoque = new Quantidade(0);

    @Column(name = "valor_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "estoque_minimo", nullable = false)
    @Builder.Default
    private int estoqueMinimo = 5;

    public boolean estaComEstoqueBaixo() {
        return quantidadeEstoque.getValor() <= estoqueMinimo;
    }

    public boolean temDisponibilidade(int qtd) {
        return quantidadeEstoque.temDisponibilidade(qtd);
    }

    public void verificarDisponibilidade(int quantidadeSolicitada) {
        if (quantidadeSolicitada <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (!temDisponibilidade(quantidadeSolicitada)) {
            throw new IllegalStateException(
                String.format("Estoque insuficiente para '%s'. Disponível: %d, Solicitado: %d",
                    nome, quantidadeEstoque.getValor(), quantidadeSolicitada)
            );
        }
    }

    public void baixarEstoque(int quantidade) {
        verificarDisponibilidade(quantidade);
        this.quantidadeEstoque = this.quantidadeEstoque.subtrair(quantidade);
    }

    public void reporEstoque(int quantidade) {
        this.quantidadeEstoque = this.quantidadeEstoque.adicionar(quantidade);
    }

    public int getQuantidadeEstoqueValor() {
        return quantidadeEstoque.getValor();
    }

    public void atualizar(String nome, String descricao, BigDecimal valorUnitario, Integer estoqueMinimo) {
        this.nome = nome;
        this.descricao = descricao;
        this.valorUnitario = valorUnitario;
        if (estoqueMinimo != null) {
            this.estoqueMinimo = estoqueMinimo;
        }
    }
}
