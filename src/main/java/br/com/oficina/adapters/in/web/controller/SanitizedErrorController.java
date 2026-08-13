package br.com.oficina.adapters.in.web.controller;

import br.com.oficina.adapters.in.web.response.ApiErrorResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SanitizedErrorController implements ErrorController {

    @RequestMapping("${server.error.path:${error.path:/error}}")
    public ResponseEntity<ApiErrorResponse> handleError(HttpServletRequest request) {
        HttpStatus status = resolveStatus(request);
        return ResponseEntity
            .status(status)
            .body(ApiErrorResponse.of(status, messageFor(status)));
    }

    private HttpStatus resolveStatus(HttpServletRequest request) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode instanceof Integer code) {
            HttpStatus status = HttpStatus.resolve(code);
            if (status != null) {
                return status;
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String messageFor(HttpStatus status) {
        return switch (status) {
            case UNAUTHORIZED -> "Nao autorizado";
            case FORBIDDEN -> "Acesso negado";
            case NOT_FOUND -> "Recurso nao encontrado";
            case METHOD_NOT_ALLOWED -> "Metodo HTTP nao permitido";
            case UNSUPPORTED_MEDIA_TYPE -> "Tipo de conteudo nao suportado";
            case NOT_ACCEPTABLE -> "Formato de resposta nao aceitavel";
            default -> status.is5xxServerError() ? "Erro interno do servidor" : "Erro na requisicao";
        };
    }
}
