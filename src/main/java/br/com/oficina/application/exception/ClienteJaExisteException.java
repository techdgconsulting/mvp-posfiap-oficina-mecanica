package br.com.oficina.application.exception;

public class ClienteJaExisteException extends NegocioException {
    public ClienteJaExisteException(String message) {
        super(message);
    }
}
