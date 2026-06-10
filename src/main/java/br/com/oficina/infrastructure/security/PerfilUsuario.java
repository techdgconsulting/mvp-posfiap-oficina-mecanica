package br.com.oficina.infrastructure.security;

/**
 * Perfis de acesso válidos no sistema.
 *
 * ATENDENTE — recepção: abre OS, aprova/rejeita orçamento, registra pagamento, entrega veículo
 * MECANICO  — oficina : inicia diagnóstico, adiciona itens, finaliza serviço
 * GESTOR    — acesso total + métricas + CRUD de peças, serviços e clientes
 */
public enum PerfilUsuario {
    ATENDENTE,
    MECANICO,
    GESTOR;

    /** Valida se a string fornecida corresponde a um perfil existente. */
    public static boolean isValido(String valor) {
        for (PerfilUsuario p : values()) {
            if (p.name().equalsIgnoreCase(valor)) return true;
        }
        return false;
    }
}
