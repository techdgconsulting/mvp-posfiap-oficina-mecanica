package br.com.oficina.domain.atendimento.veiculo.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Placa {

    private static final Pattern MERCOSUL = Pattern.compile("^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$");

    @Column(name = "placa", nullable = false, unique = true, length = 7)
    private String valor;

    public Placa(String valor) {
        String limpo = valor.toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (!MERCOSUL.matcher(limpo).matches()) {
            throw new IllegalArgumentException(
                "Placa inválida. Use formato Mercosul (ABC1D23) ou antigo (ABC1234): " + valor
            );
        }
        this.valor = limpo;
    }

    public String formatado() {
        return valor.substring(0, 3) + "-" + valor.substring(3);
    }
}
