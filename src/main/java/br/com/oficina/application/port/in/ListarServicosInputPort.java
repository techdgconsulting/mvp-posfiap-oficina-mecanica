package br.com.oficina.application.port.in;

import br.com.oficina.application.query.ServicoResult;
import java.util.List;

public interface ListarServicosInputPort {
    List<ServicoResult> execute();
}
