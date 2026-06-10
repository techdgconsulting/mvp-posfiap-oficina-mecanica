package br.com.oficina.domain.execucao.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PeriodoExecucao {

    private LocalDateTime inicio;
    private LocalDateTime fim;

    public PeriodoExecucao(LocalDateTime inicio) {
        if (inicio == null) {
            throw new IllegalArgumentException("Data de início não pode ser nula");
        }
        this.inicio = inicio;
    }

    public void finalizarEm(LocalDateTime fim) {
        if (fim == null) {
            throw new IllegalArgumentException("Data de fim não pode ser nula");
        }
        if (fim.isBefore(this.inicio)) {
            throw new IllegalArgumentException("Data de fim não pode ser anterior à data de início");
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
