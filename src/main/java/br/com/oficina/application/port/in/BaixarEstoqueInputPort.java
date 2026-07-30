package br.com.oficina.application.port.in;

import br.com.oficina.application.command.BaixarEstoqueCommand;
import br.com.oficina.application.query.PecaResult;

public interface BaixarEstoqueInputPort {
    PecaResult execute(BaixarEstoqueCommand command);
}
