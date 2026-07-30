package br.com.oficina.application.command;

public record RegistrarUsuarioCommand(String username, String password, String role) {}
