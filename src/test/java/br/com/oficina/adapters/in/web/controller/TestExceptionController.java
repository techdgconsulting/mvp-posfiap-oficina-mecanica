package br.com.oficina.adapters.in.web.controller;

import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.domain.exception.DocumentoInvalidoException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import java.util.Map;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-exceptions")
public class TestExceptionController {

    @GetMapping("/not-found")
    public void notFound() {
        throw new RecursoNaoEncontradoException("Recurso nao encontrado");
    }

    @GetMapping("/negocio")
    public void negocio() {
        throw new NegocioException("Regra de negocio violada");
    }

    @GetMapping("/documento-invalido")
    public void documentoInvalido() {
        throw new DocumentoInvalidoException("CPF invalido");
    }

    @GetMapping("/bad-credentials")
    public void badCredentials() {
        throw new BadCredentialsException("credenciais invalidas");
    }

    @GetMapping("/illegal-arg")
    public void illegalArg() {
        throw new IllegalArgumentException("argumento invalido");
    }

    @GetMapping("/illegal-state")
    public void illegalState() {
        throw new IllegalStateException("estado invalido");
    }

    @PostMapping(value = "/json-invalido", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void jsonInvalido(@RequestBody Object body) {
    }

    @GetMapping("/missing-param")
    public void missingParam(@RequestParam String nome) {
    }

    @GetMapping(value = "/json-only", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> jsonOnly() {
        return Map.of("status", "ok");
    }

    @GetMapping("/media-type-not-acceptable")
    public void mediaTypeNotAcceptable() throws HttpMediaTypeNotAcceptableException {
        throw new HttpMediaTypeNotAcceptableException("formato nao aceitavel");
    }

    @GetMapping("/missing-path-variable")
    public void missingPathVariable() throws NoSuchMethodException, MissingPathVariableException {
        MethodParameter parameter = new MethodParameter(
            TestExceptionController.class.getDeclaredMethod("pathVariableFixture", Long.class),
            0
        );
        throw new MissingPathVariableException("id", parameter);
    }

    public void pathVariableFixture(@PathVariable Long id) {
    }

    @GetMapping("/constraint-violation")
    public void constraintViolation() {
        throw new ConstraintViolationException("violacao", Set.of());
    }

    @GetMapping("/bind-exception")
    public void bindException() throws org.springframework.validation.BindException {
        var target = new Object();
        var bindingResult = new org.springframework.validation.BeanPropertyBindingResult(target, "target");
        bindingResult.rejectValue(null, "error", "campo invalido");
        throw new org.springframework.validation.BindException(bindingResult);
    }

    @GetMapping("/data-integrity")
    public void dataIntegrity() {
        throw new DataIntegrityViolationException("violacao de integridade");
    }

    @GetMapping("/entity-not-found")
    public void entityNotFound() {
        throw new EntityNotFoundException("entidade nao encontrada");
    }

    @GetMapping("/generico")
    public void generico() {
        throw new RuntimeException("erro inesperado");
    }

    @GetMapping("/type-mismatch/{id}")
    public void typeMismatch(@PathVariable Long id) {
    }
}
