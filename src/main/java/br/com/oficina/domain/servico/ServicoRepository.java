package br.com.oficina.domain.servico;

import java.util.List;
import java.util.Optional;

public interface ServicoRepository {
    Servico salvar(Servico servico);
    Optional<Servico> buscarPorId(Long id);
    List<Servico> listarTodos();
    void excluir(Long id);
}
