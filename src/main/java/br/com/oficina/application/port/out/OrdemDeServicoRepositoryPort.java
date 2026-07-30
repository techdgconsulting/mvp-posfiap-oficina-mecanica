package br.com.oficina.application.port.out;

import br.com.oficina.domain.model.OrdemDeServico;
import br.com.oficina.domain.valueobject.StatusOS;
import java.util.List;
import java.util.Optional;

public interface OrdemDeServicoRepositoryPort {
    OrdemDeServico salvar(OrdemDeServico ordemDeServico);
    Optional<OrdemDeServico> buscarPorId(Long id);
    Optional<OrdemDeServico> buscarPorNumero(String numero);
    List<OrdemDeServico> listarTodas();
    List<OrdemDeServico> listarFilaOperacional();
    List<OrdemDeServico> listarPorCliente(Long clienteId);
    List<OrdemDeServico> listarPorStatus(StatusOS status);
    List<OrdemDeServico> listarPorVeiculo(Long veiculoId);
}
