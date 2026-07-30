package br.com.oficina.application.port.in;

import br.com.oficina.application.query.ClienteResult;
import java.util.List;

public interface ListarClientesInputPort {
    List<ClienteResult> execute();
}
