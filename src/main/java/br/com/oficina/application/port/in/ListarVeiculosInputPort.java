package br.com.oficina.application.port.in;

import br.com.oficina.application.query.VeiculoResult;
import java.util.List;

public interface ListarVeiculosInputPort {
    List<VeiculoResult> execute();
}
