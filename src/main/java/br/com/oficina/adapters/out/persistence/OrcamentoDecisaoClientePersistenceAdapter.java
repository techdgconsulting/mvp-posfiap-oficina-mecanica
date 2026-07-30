package br.com.oficina.adapters.out.persistence;

import br.com.oficina.adapters.out.persistence.mapper.OrcamentoDecisaoClientePersistenceMapper;
import br.com.oficina.adapters.out.persistence.repository.SpringDataOrcamentoDecisaoClienteRepository;
import br.com.oficina.application.port.out.OrcamentoDecisaoClienteRepositoryPort;
import br.com.oficina.domain.model.OrcamentoDecisaoCliente;
import br.com.oficina.domain.valueobject.StatusDecisaoCliente;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrcamentoDecisaoClientePersistenceAdapter implements OrcamentoDecisaoClienteRepositoryPort {

    private final SpringDataOrcamentoDecisaoClienteRepository repository;
    private final OrcamentoDecisaoClientePersistenceMapper mapper;

    @Override
    public OrcamentoDecisaoCliente salvar(OrcamentoDecisaoCliente decisao) {
        return mapper.toDomain(repository.save(mapper.toEntity(decisao)));
    }

    @Override
    public Optional<OrcamentoDecisaoCliente> buscarPorTokenHash(String tokenHash) {
        return repository.findByTokenHash(tokenHash).map(mapper::toDomain);
    }

    @Override
    public List<OrcamentoDecisaoCliente> listarPorOrcamentoEStatus(Long orcamentoId, StatusDecisaoCliente status) {
        return repository.findByOrcamentoIdAndStatus(orcamentoId, status).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
