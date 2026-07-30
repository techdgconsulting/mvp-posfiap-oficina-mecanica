package br.com.oficina.application.port.out;

import br.com.oficina.domain.model.OrcamentoDecisaoCliente;
import br.com.oficina.domain.valueobject.StatusDecisaoCliente;
import java.util.List;
import java.util.Optional;

public interface OrcamentoDecisaoClienteRepositoryPort {
    OrcamentoDecisaoCliente salvar(OrcamentoDecisaoCliente decisao);
    Optional<OrcamentoDecisaoCliente> buscarPorTokenHash(String tokenHash);
    List<OrcamentoDecisaoCliente> listarPorOrcamentoEStatus(Long orcamentoId, StatusDecisaoCliente status);
}
