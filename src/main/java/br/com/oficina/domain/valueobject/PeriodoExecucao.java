package br.com.oficina.domain.valueobject;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoExecucao {

    private LocalDateTime inicio;
    private LocalDateTime fim;

    public PeriodoExecucao(LocalDateTime inicio) {
        if (inicio == null) {
            throw new IllegalArgumentException("Data de inicio nao pode ser nula");
        }
        this.inicio = inicio;
    }

    public void finalizarEm(LocalDateTime fim) {
        if (fim == null) {
            throw new IllegalArgumentException("Data de fim nao pode ser nula");
        }
        if (fim.isBefore(this.inicio)) {
            throw new IllegalArgumentException("Data de fim nao pode ser anterior a data de inicio");
        }
        this.fim = fim;
    }

    public Duration calcularDuracao() {
        if (inicio == null) {
            return Duration.ZERO;
        }
        LocalDateTime fimCalculo = fim != null ? fim : LocalDateTime.now();
        return Duration.between(inicio, fimCalculo);
    }

    public boolean isFinalizado() {
        return fim != null;
    }
}
