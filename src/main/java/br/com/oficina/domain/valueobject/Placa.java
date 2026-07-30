package br.com.oficina.domain.valueobject;

import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Placa {

    private static final Pattern MERCOSUL = Pattern.compile("^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$");

    private final String valor;

    public Placa(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Placa e obrigatoria");
        }
        String limpo = valor.toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (!MERCOSUL.matcher(limpo).matches()) {
            throw new IllegalArgumentException(
                "Placa invalida. Use formato Mercosul (ABC1D23) ou antigo (ABC1234): " + valor
            );
        }
        this.valor = limpo;
    }

    public String formatado() {
        return valor.substring(0, 3) + "-" + valor.substring(3);
    }
}
