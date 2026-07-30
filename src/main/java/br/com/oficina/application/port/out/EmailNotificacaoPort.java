package br.com.oficina.application.port.out;

public interface EmailNotificacaoPort {
    void enviar(EmailNotificacao mensagem);

    record EmailNotificacao(String destinatario, String assunto, String corpo) {}
}
