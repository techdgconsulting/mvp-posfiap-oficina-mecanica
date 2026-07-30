package br.com.oficina.application.port.out;

import br.com.oficina.domain.model.Servico;
import java.util.List;
import java.util.Optional;

public interface ServicoRepositoryPort {
    Servico salvar(Servico servico);
    Optional<Servico> buscarPorId(Long id);
    List<Servico> listarTodos();
    void excluir(Long id);
}
