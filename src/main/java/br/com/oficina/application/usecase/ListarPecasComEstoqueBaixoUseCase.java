package br.com.oficina.application.usecase;

import br.com.oficina.application.port.in.ListarPecasComEstoqueBaixoInputPort;
import br.com.oficina.application.port.out.PecaRepositoryPort;
import br.com.oficina.application.query.PecaResult;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListarPecasComEstoqueBaixoUseCase implements ListarPecasComEstoqueBaixoInputPort {

    private final PecaRepositoryPort pecaRepository;

    @Override
    public List<PecaResult> executeEstoqueBaixo() {
        return pecaRepository.listarComEstoqueBaixo().stream().map(PecaResultMapper::toResult).toList();
    }
}
