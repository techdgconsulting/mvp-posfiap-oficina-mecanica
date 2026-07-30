package br.com.oficina.adapters.out.notification;

import br.com.oficina.application.port.out.EmailNotificacaoPort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SmtpEmailNotificacaoAdapterTest {

    @Test
    void deveEnviarEmailUsandoJavaMailSender() {
        var mailSender = mock(JavaMailSender.class);
        var adapter = new SmtpEmailNotificacaoAdapter(mailSender, "no-reply@oficina.local");

        adapter.enviar(new EmailNotificacaoPort.EmailNotificacao(
                "cliente@email.com",
                "Atualizacao da OS",
                "Corpo informativo"));

        var captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        var message = captor.getValue();
        assertEquals("no-reply@oficina.local", message.getFrom());
        assertArrayEquals(new String[]{"cliente@email.com"}, message.getTo());
        assertEquals("Atualizacao da OS", message.getSubject());
        assertEquals("Corpo informativo", message.getText());
    }
}
