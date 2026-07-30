package br.com.oficina.adapters.out.notification;

import br.com.oficina.application.port.out.EmailNotificacaoPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnExpression("'${oficina.email.enabled:true}' == 'true' && '${oficina.email.mode:LOG}' == 'SMTP'")
public class SmtpEmailNotificacaoAdapter implements EmailNotificacaoPort {

    private final JavaMailSender mailSender;
    private final String remetente;

    public SmtpEmailNotificacaoAdapter(
            JavaMailSender mailSender,
            @Value("${oficina.email.remetente:no-reply@oficina.local}") String remetente) {
        this.mailSender = mailSender;
        this.remetente = remetente;
    }

    @Override
    public void enviar(EmailNotificacao mensagem) {
        var mail = new SimpleMailMessage();
        mail.setFrom(remetente);
        mail.setTo(mensagem.destinatario());
        mail.setSubject(mensagem.assunto());
        mail.setText(mensagem.corpo());
        mailSender.send(mail);
    }
}
