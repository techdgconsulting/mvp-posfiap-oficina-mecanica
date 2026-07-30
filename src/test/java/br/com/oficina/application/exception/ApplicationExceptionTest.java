package br.com.oficina.application.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationExceptionTest {

    @Test
    void devePreservarMensagemDasExcecoesDeAplicacao() {
        assertEquals("Cliente ja existe", new ClienteJaExisteException("Cliente ja existe").getMessage());
        assertEquals("Credenciais invalidas", new CredenciaisInvalidasException().getMessage());
        assertEquals("Endereco nao encontrado", new EnderecoNaoEncontradoException("Endereco nao encontrado").getMessage());
        assertEquals("Regra de negocio violada", new NegocioException("Regra de negocio violada").getMessage());
        assertEquals("Recurso nao encontrado", new RecursoNaoEncontradoException("Recurso nao encontrado").getMessage());
    }
}
