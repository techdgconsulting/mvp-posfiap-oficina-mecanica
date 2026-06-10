package br.com.oficina.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // endpoints públicos
                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/auth/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/api-docs/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/v3/api-docs/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui.html")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                // Necessário para error dispatch do Tomcat (403 → /error) não ser sobrescrito por 401
                .requestMatchers(AntPathRequestMatcher.antMatcher("/error")).permitAll()

                // consulta de status pelo número da OS (público, sem token) — usa numero da OS, não ID interno
                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/ordens-servico/numero/*/status")).permitAll()

                // GESTOR only — catálogo de peças e serviços
                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/pecas/**")).hasRole("GESTOR")
                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/servicos/**")).hasRole("GESTOR")

                // GESTOR only — OS: listar todas, listar por status, métricas
                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/ordens-servico")).hasRole("GESTOR")
                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/ordens-servico/status/*")).hasRole("GESTOR")
                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/ordens-servico/metricas/*")).hasRole("GESTOR")
                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/ordens-servico/*/metricas")).hasRole("GESTOR")

                // MECANICO ou GESTOR — diagnóstico e execução
                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PATCH, "/api/ordens-servico/*/iniciar-diagnostico")).hasAnyRole("MECANICO", "GESTOR")
                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PATCH, "/api/ordens-servico/*/finalizar")).hasAnyRole("MECANICO", "GESTOR")
                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/ordens-servico/*/itens")).hasAnyRole("MECANICO", "GESTOR")

                // ATENDENTE ou GESTOR — recepção e faturamento
                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/ordens-servico")).hasAnyRole("ATENDENTE", "GESTOR")
                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/ordens-servico/*/orcamento")).hasAnyRole("ATENDENTE", "GESTOR")
                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/ordens-servico/*/pagamento")).hasAnyRole("ATENDENTE", "GESTOR")
                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PATCH, "/api/ordens-servico/*/aprovar")).hasAnyRole("ATENDENTE", "GESTOR")
                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PATCH, "/api/ordens-servico/*/rejeitar")).hasAnyRole("ATENDENTE", "GESTOR")
                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PATCH, "/api/ordens-servico/*/entregar")).hasAnyRole("ATENDENTE", "GESTOR")
                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/clientes/**")).hasAnyRole("ATENDENTE", "GESTOR")
                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/veiculos/**")).hasAnyRole("ATENDENTE", "GESTOR")

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
