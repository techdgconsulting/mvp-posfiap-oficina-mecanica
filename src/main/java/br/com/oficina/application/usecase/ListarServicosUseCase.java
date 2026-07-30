package br.com.oficina.application.usecase;

import br.com.oficina.application.port.in.ListarServicosInputPort;
import br.com.oficina.application.port.out.ServicoRepositoryPort;
import br.com.oficina.application.query.ServicoResult;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListarServicosUseCase implements ListarServicosInputPort {

    private final ServicoRepositoryPort servicoRepository;

    @Override
    public List<ServicoResult> execute() {
        return servicoRepository.listarTodos().stream()
                .map(ServicoResultMapper::toResult)
                .toList();
    }
}
