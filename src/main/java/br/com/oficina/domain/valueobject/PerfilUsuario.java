package br.com.oficina.domain.valueobject;

public enum PerfilUsuario {
    ATENDENTE,
    MECANICO,
    GESTOR;

    public static PerfilUsuario from(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Perfil inválido. Use: ATENDENTE, MECANICO ou GESTOR");
        }
        for (PerfilUsuario perfil : values()) {
            if (perfil.name().equalsIgnoreCase(valor)) {
                return perfil;
            }
        }
        throw new IllegalArgumentException("Perfil inválido. Use: ATENDENTE, MECANICO ou GESTOR");
    }

    public static boolean isValido(String valor) {
        try {
            from(valor);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
