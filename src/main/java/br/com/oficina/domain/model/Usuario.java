package br.com.oficina.domain.model;

import br.com.oficina.domain.valueobject.PerfilUsuario;

public class Usuario {

    private Long id;
    private String username;
    private String password;
    private PerfilUsuario perfil;

    public Usuario() {
    }

    public Usuario(Long id, String username, String password, PerfilUsuario perfil) {
        validarUsername(username);
        validarPassword(password);
        if (perfil == null) {
            throw new IllegalArgumentException("Perfil obrigatorio");
        }
        this.id = id;
        this.username = username;
        this.password = password;
        this.perfil = perfil;
    }

    public static Usuario criar(String username, String password, String role) {
        return new Usuario(null, username, password, PerfilUsuario.from(role));
    }

    private void validarUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username obrigatorio");
        }
    }

    private void validarPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password obrigatorio");
        }
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public String getRole() {
        return perfil.name();
    }
}
