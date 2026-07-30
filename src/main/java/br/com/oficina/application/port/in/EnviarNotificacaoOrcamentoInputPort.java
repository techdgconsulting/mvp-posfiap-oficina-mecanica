package br.com.oficina.application.port.in;

import br.com.oficina.application.command.EnviarNotificacaoOrcamentoCommand;
import br.com.oficina.application.query.NotificacaoOrcamentoResult;

public interface EnviarNotificacaoOrcamentoInputPort {
    NotificacaoOrcamentoResult execute(EnviarNotificacaoOrcamentoCommand command);
}
