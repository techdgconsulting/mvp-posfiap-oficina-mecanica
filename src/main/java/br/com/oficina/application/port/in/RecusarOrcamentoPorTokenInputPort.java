package br.com.oficina.application.port.in;

import br.com.oficina.application.command.DecidirOrcamentoPorTokenCommand;
import br.com.oficina.application.query.DecisaoOrcamentoClienteResult;

public interface RecusarOrcamentoPorTokenInputPort {
    DecisaoOrcamentoClienteResult executeRecusar(DecidirOrcamentoPorTokenCommand command);
}
