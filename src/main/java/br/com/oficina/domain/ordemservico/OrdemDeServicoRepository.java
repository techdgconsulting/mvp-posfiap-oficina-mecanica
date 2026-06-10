package br.com.oficina.domain.ordemservico;

import java.util.List;
import java.util.Optional;

public interface OrdemDeServicoRepository {
    OrdemDeServico salvar(OrdemDeServico ordemDeServico);
    Optional<OrdemDeServico> buscarPorId(Long id);
    Optional<OrdemDeServico> buscarPorNumero(String numero);
    List<OrdemDeServico> listarTodas();
    List<OrdemDeServico> listarPorCliente(Long clienteId);
    List<OrdemDeServico> listarPorStatus(StatusOS status);
    List<OrdemDeServico> listarPorVeiculo(Long veiculoId);
}
