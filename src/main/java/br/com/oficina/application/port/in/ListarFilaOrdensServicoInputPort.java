package br.com.oficina.application.port.in;

import br.com.oficina.application.query.OrdemServicoResult;
import java.util.List;

public interface ListarFilaOrdensServicoInputPort {
    List<OrdemServicoResult> listarFilaOperacional();
}
