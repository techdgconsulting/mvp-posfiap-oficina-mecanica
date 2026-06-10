package br.com.oficina.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Oficina Mecanica DGCAR - API",
                version = "1.0.0",
                description = "API REST para gestao de oficina mecanica: clientes, veiculos, pecas, servicos e ordens de servico. " +
                        "Autenticacao via JWT Bearer Token. Fluxo da OS: RECEBIDA -> EM_DIAGNOSTICO -> AGUARDANDO_APROVACAO -> EM_EXECUCAO -> FINALIZADA -> AGUARDANDO_RETIRADA -> ENTREGUE. " +
                        "Endpoint publico (sem token): GET /api/ordens-servico/numero/{numero}/status - permite ao cliente rastrear o status da OS pelo numero impresso no comprovante.",
                contact = @Contact(name = "Tech Challenge FIAP - Grupo DGCAR")
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Informe o token JWT obtido no endpoint POST /api/auth/login"
)
public class OpenApiConfig {
}
