package br.com.oficina.application.port.out;

public interface TokenProviderPort {
    String gerarToken(String username, String role);
    String extrairUsername(String token);
    String extrairRole(String token);
    boolean isTokenValido(String token);
}
