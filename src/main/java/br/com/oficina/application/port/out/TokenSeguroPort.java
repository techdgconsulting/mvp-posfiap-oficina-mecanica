package br.com.oficina.application.port.out;

public interface TokenSeguroPort {
    String gerarToken();
    String gerarHash(String token);
}
