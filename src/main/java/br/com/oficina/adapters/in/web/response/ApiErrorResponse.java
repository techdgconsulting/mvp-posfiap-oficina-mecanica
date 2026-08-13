package br.com.oficina.adapters.in.web.response;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public record ApiErrorResponse(
    String timestamp,
    int status,
    String erro
) {

    public static ApiErrorResponse of(HttpStatus status, String erro) {
        return new ApiErrorResponse(LocalDateTime.now().toString(), status.value(), erro);
    }
}
