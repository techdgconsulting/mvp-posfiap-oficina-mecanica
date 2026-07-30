package br.com.oficina.domain.exception;

public class DocumentoInvalidoException extends IllegalArgumentException {
    public DocumentoInvalidoException(String message) {
        super(message);
    }
}
