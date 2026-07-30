package br.com.oficina.application.port.in;

import br.com.oficina.application.command.NotificarStatusOrdemServicoCommand;
import br.com.oficina.application.query.NotificacaoStatusOrdemServicoResult;

public interface NotificarStatusOrdemServicoInputPort {
    NotificacaoStatusOrdemServicoResult execute(NotificarStatusOrdemServicoCommand command);
}
