package br.com.oficina.adapters.out.notification;

import br.com.oficina.application.port.out.EmailNotificacaoPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnExpression("'${oficina.email.enabled:true}' == 'true' && '${oficina.email.mode:LOG}' == 'LOG'")
public class LogEmailNotificacaoAdapter implements EmailNotificacaoPort {

    @Override
    public void enviar(EmailNotificacao mensagem) {
        log.info("EMAIL_NOTIFICACAO destinatario={} assunto={} corpo={}",
                mensagem.destinatario(),
                mensagem.assunto(),
                mensagem.corpo());
    }
}
