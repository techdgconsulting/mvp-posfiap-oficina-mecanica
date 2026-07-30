package br.com.oficina.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        var paths = PathPatternRequestMatcher.withDefaults();

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // endpoints públicos
                .requestMatchers(paths.matcher("/api/auth/**")).permitAll()
                .requestMatchers(paths.matcher("/swagger-ui/**")).permitAll()
                .requestMatchers(paths.matcher("/api-docs.yaml")).permitAll()
                .requestMatchers(paths.matcher("/api-docs/**")).permitAll()
                .requestMatchers(paths.matcher("/v3/api-docs.yaml")).permitAll()
                .requestMatchers(paths.matcher("/v3/api-docs/**")).permitAll()
                .requestMatchers(paths.matcher("/swagger-ui.html")).permitAll()
                .requestMatchers(paths.matcher("/h2-console/**")).permitAll()
                .requestMatchers(paths.matcher("/actuator/health/**")).permitAll()
                .requestMatchers(paths.matcher("/actuator/info")).permitAll()
                // Necessário para error dispatch do Tomcat (403 → /error) não ser sobrescrito por 401
                .requestMatchers(paths.matcher("/error")).permitAll()

                // consulta de status pelo número da OS (público, sem token) — usa numero da OS, não ID interno
                .requestMatchers(paths.matcher(HttpMethod.GET, "/api/ordens-servico/numero/*/status")).permitAll()
                // decisão externa de orçamento por token opaco enviado ao cliente
                .requestMatchers(paths.matcher(HttpMethod.POST, "/api/orcamentos/decisoes-cliente/*/aprovar")).permitAll()
                .requestMatchers(paths.matcher(HttpMethod.POST, "/api/orcamentos/decisoes-cliente/*/recusar")).permitAll()

                // GESTOR only — catálogo de peças e serviços
                .requestMatchers(paths.matcher("/api/pecas/**")).hasRole("GESTOR")
                .requestMatchers(paths.matcher("/api/servicos/**")).hasRole("GESTOR")

                // fila operacional — acompanhamento por atendimento, mecânica e gestão
                .requestMatchers(paths.matcher(HttpMethod.GET, "/api/ordens-servico/fila")).hasAnyRole("ATENDENTE", "MECANICO", "GESTOR")

                // GESTOR only — OS: listar todas, listar por status, métricas
                .requestMatchers(paths.matcher(HttpMethod.GET, "/api/ordens-servico")).hasRole("GESTOR")
                .requestMatchers(paths.matcher(HttpMethod.GET, "/api/ordens-servico/status/*")).hasRole("GESTOR")
                .requestMatchers(paths.matcher(HttpMethod.GET, "/api/ordens-servico/metricas/*")).hasRole("GESTOR")
                .requestMatchers(paths.matcher(HttpMethod.GET, "/api/ordens-servico/*/metricas")).hasRole("GESTOR")

                // MECANICO ou GESTOR — diagnóstico e execução
                .requestMatchers(paths.matcher(HttpMethod.PATCH, "/api/ordens-servico/*/iniciar-diagnostico")).hasAnyRole("MECANICO", "GESTOR")
                .requestMatchers(paths.matcher(HttpMethod.PATCH, "/api/ordens-servico/*/finalizar")).hasAnyRole("MECANICO", "GESTOR")
                .requestMatchers(paths.matcher(HttpMethod.POST, "/api/ordens-servico/*/itens")).hasAnyRole("MECANICO", "GESTOR")

                // ATENDENTE ou GESTOR — recepção e faturamento
                .requestMatchers(paths.matcher(HttpMethod.POST, "/api/ordens-servico")).hasAnyRole("ATENDENTE", "GESTOR")
                .requestMatchers(paths.matcher(HttpMethod.POST, "/api/ordens-servico/completa")).hasAnyRole("ATENDENTE", "GESTOR")
                .requestMatchers(paths.matcher(HttpMethod.POST, "/api/ordens-servico/*/orcamento")).hasAnyRole("ATENDENTE", "GESTOR")
                .requestMatchers(paths.matcher(HttpMethod.POST, "/api/ordens-servico/*/orcamento/notificar-cliente")).hasAnyRole("ATENDENTE", "GESTOR")
                .requestMatchers(paths.matcher(HttpMethod.POST, "/api/ordens-servico/*/pagamento")).hasAnyRole("ATENDENTE", "GESTOR")
                .requestMatchers(paths.matcher(HttpMethod.PATCH, "/api/ordens-servico/*/aprovar")).hasAnyRole("ATENDENTE", "GESTOR")
                .requestMatchers(paths.matcher(HttpMethod.PATCH, "/api/ordens-servico/*/rejeitar")).hasAnyRole("ATENDENTE", "GESTOR")
                .requestMatchers(paths.matcher(HttpMethod.PATCH, "/api/ordens-servico/*/entregar")).hasAnyRole("ATENDENTE", "GESTOR")
                .requestMatchers(paths.matcher("/api/clientes/**")).hasAnyRole("ATENDENTE", "GESTOR")
                .requestMatchers(paths.matcher("/api/veiculos/**")).hasAnyRole("ATENDENTE", "GESTOR")

                // demais endpoints autenticados (GET OS por id/numero, etc.)
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            // 401 para requests sem autenticação, 403 para autenticado sem permissão
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), "Não autorizado"))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    response.sendError(HttpStatus.FORBIDDEN.value(), "Acesso negado")))
            // pra funcionar o h2-console
            .headers(headers -> headers.frameOptions(fo -> fo.sameOrigin()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
