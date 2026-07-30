package br.com.oficina.application.port.out;

import br.com.oficina.domain.model.Peca;
import java.util.List;
import java.util.Optional;

public interface PecaRepositoryPort {
    Optional<Peca> buscarPorId(Long id);
    Peca salvar(Peca peca);
    List<Peca> listarTodas();
    List<Peca> listarComEstoqueBaixo();
    void excluir(Long id);
}
