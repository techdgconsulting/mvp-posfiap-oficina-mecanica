package br.com.oficina.adapters.out.notification;

import br.com.oficina.application.port.out.EmailNotificacaoPort;
import org.junit.jupiter.api.Test;

class DisabledEmailNotificacaoAdapterTest {

    @Test
    void deveIgnorarEnvioQuandoEmailEstaDesativado() {
        var adapter = new DisabledEmailNotificacaoAdapter();

        adapter.enviar(new EmailNotificacaoPort.EmailNotificacao(
                "cliente@email.com",
                "Atualizacao",
                "Corpo"));
    }
}
