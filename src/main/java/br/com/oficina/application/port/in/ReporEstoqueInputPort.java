package br.com.oficina.application.port.in;

import br.com.oficina.application.command.ReporEstoqueCommand;
import br.com.oficina.application.query.PecaResult;

public interface ReporEstoqueInputPort {
    PecaResult execute(ReporEstoqueCommand command);
}
