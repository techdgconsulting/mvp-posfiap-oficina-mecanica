package br.com.oficina.adapters.out.notification;

import br.com.oficina.application.port.out.EmailNotificacaoPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "oficina.email.enabled", havingValue = "false")
public class DisabledEmailNotificacaoAdapter implements EmailNotificacaoPort {

    @Override
    public void enviar(EmailNotificacao mensagem) {
        log.info("EMAIL_NOTIFICACAO_DESATIVADA destinatario={} assunto={}",
                mensagem.destinatario(),
                mensagem.assunto());
    }
}
