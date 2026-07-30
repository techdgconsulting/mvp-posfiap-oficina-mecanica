package br.com.oficina.application.port.in;

import br.com.oficina.application.query.PecaResult;
import java.util.List;

public interface ListarPecasInputPort {
    List<PecaResult> execute();
}
