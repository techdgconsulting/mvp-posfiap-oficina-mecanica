package br.com.oficina.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class Quantidade {

    private int valor;

    public Quantidade(int valor) {
        validar(valor);
        this.valor = valor;
    }

    public boolean validar() {
        return valor >= 0;
    }

    private void validar(int valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("Quantidade nao pode ser negativa: " + valor);
        }
    }

    public Quantidade subtrair(int qtd) {
        if (qtd <= 0) {
            throw new IllegalArgumentException("Quantidade para subtracao deve ser maior que zero");
        }
        if (qtd > this.valor) {
            throw new IllegalStateException(
                String.format("Quantidade insuficiente. Disponivel: %d, Solicitado: %d", this.valor, qtd)
            );
        }
        return new Quantidade(this.valor - qtd);
    }

    public Quantidade adicionar(int qtd) {
        if (qtd <= 0) {
            throw new IllegalArgumentException("Quantidade para adicao deve ser maior que zero");
        }
        return new Quantidade(this.valor + qtd);
    }

    public boolean temDisponibilidade(int qtd) {
        return this.valor >= qtd;
    }

    @Override
    public String toString() {
        return String.valueOf(valor);
    }
}
