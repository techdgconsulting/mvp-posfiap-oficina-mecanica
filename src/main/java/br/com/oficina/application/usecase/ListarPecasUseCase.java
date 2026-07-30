package br.com.oficina.application.usecase;

import br.com.oficina.application.port.in.ListarPecasInputPort;
import br.com.oficina.application.port.out.PecaRepositoryPort;
import br.com.oficina.application.query.PecaResult;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListarPecasUseCase implements ListarPecasInputPort {

    private final PecaRepositoryPort pecaRepository;

    @Override
    public List<PecaResult> execute() {
        return pecaRepository.listarTodas().stream().map(PecaResultMapper::toResult).toList();
    }
}
