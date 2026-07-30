package br.com.oficina.domain.valueobject;

import br.com.oficina.domain.exception.DocumentoInvalidoException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class CpfCnpj {

    private final String valor;
    private final TipoDocumento tipo;

    public CpfCnpj(String valor) {
        if (valor == null) {
            throw new DocumentoInvalidoException("Documento e obrigatorio");
        }
        String limpo = valor.replaceAll("[^\\d]", "");
        if (limpo.length() == 11) {
            validarCpf(limpo);
            this.tipo = TipoDocumento.CPF;
        } else if (limpo.length() == 14) {
            validarCnpj(limpo);
            this.tipo = TipoDocumento.CNPJ;
        } else {
            throw new DocumentoInvalidoException("Documento deve ter 11 (CPF) ou 14 (CNPJ) digitos");
        }
        this.valor = limpo;
    }

    private void validarCpf(String cpf) {
        if (cpf.chars().distinct().count() == 1) {
            throw new DocumentoInvalidoException("CPF invalido: todos os digitos iguais");
        }

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) primeiroDigito = 0;

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) segundoDigito = 0;

        if (Character.getNumericValue(cpf.charAt(9)) != primeiroDigito ||
            Character.getNumericValue(cpf.charAt(10)) != segundoDigito) {
            throw new DocumentoInvalidoException("CPF invalido");
        }
    }

    private void validarCnpj(String cnpj) {
        if (cnpj.chars().distinct().count() == 1) {
            throw new DocumentoInvalidoException("CNPJ invalido: todos os digitos iguais");
        }

        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int soma = 0;
        for (int i = 0; i < 12; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesos1[i];
        }
        int primeiroDigito = soma % 11 < 2 ? 0 : 11 - (soma % 11);

        soma = 0;
        for (int i = 0; i < 13; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesos2[i];
        }
        int segundoDigito = soma % 11 < 2 ? 0 : 11 - (soma % 11);

        if (Character.getNumericValue(cnpj.charAt(12)) != primeiroDigito ||
            Character.getNumericValue(cnpj.charAt(13)) != segundoDigito) {
            throw new DocumentoInvalidoException("CNPJ invalido");
        }
    }

    public String formatado() {
        if (tipo == TipoDocumento.CPF) {
            return valor.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }
        return valor.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }

    public enum TipoDocumento {
        CPF, CNPJ
    }
}
