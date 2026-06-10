package br.com.oficina.application.exception;

// exceção genérica pra regra de negócio
public class NegocioException extends RuntimeException {
    public NegocioException(String msg) {
        super(msg);
    }
}
