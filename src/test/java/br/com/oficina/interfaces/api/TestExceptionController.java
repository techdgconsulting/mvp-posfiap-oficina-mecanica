package br.com.oficina.interfaces.api;

import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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

    @PostMapping("/json-invalido")
    public void jsonInvalido(@RequestBody Object body) {
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