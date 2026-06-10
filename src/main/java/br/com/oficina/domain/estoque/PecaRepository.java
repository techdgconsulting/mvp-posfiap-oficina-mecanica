package br.com.oficina.domain.estoque;

import java.util.List;
import java.util.Optional;

public interface PecaRepository {
    Peca salvar(Peca peca);
    Optional<Peca> buscarPorId(Long id);
    List<Peca> listarTodas();
    List<Peca> listarComEstoqueBaixo();
    void excluir(Long id);
}
