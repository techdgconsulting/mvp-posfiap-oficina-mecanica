package br.com.oficina.application.exception;

public class EnderecoNaoEncontradoException extends RuntimeException {
    public EnderecoNaoEncontradoException(String message) {
        super(message);
    }
}
