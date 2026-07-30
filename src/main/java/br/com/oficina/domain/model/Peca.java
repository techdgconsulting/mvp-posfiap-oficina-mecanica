package br.com.oficina.domain.model;

import br.com.oficina.domain.valueobject.Quantidade;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Peca {

    private Long id;
    private String nome;
    private String descricao;
    @Builder.Default
    private Quantidade quantidadeEstoque = new Quantidade(0);
    private BigDecimal valorUnitario;
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
                String.format("Estoque insuficiente para '%s'. Disponivel: %d, Solicitado: %d",
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

    public void alterarQuantidadeEstoque(Quantidade quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }
}
