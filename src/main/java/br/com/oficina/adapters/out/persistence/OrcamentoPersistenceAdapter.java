package br.com.oficina.adapters.out.persistence;

import br.com.oficina.adapters.out.persistence.mapper.OrcamentoPersistenceMapper;
import br.com.oficina.adapters.out.persistence.repository.SpringDataOrcamentoRepository;
import br.com.oficina.application.port.out.OrcamentoRepositoryPort;
import br.com.oficina.domain.model.Orcamento;
import br.com.oficina.domain.valueobject.StatusOrcamento;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrcamentoPersistenceAdapter implements OrcamentoRepositoryPort {

    private final SpringDataOrcamentoRepository repository;
    private final OrcamentoPersistenceMapper mapper;

    @Override
    public Orcamento salvar(Orcamento orcamento) {
        return mapper.toDomain(repository.save(mapper.toEntity(orcamento)));
    }

    @Override
    public Optional<Orcamento> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Orcamento> listarPorOrdemDeServico(Long ordemServicoId) {
        return repository.findByOrdemDeServicoId(ordemServicoId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Orcamento> buscarAtivoByOrdemDeServico(Long ordemServicoId) {
        return repository.findFirstByOrdemDeServicoIdAndStatusIn(
                ordemServicoId,
                List.of(StatusOrcamento.PENDENTE, StatusOrcamento.ENVIADO))
                .map(mapper::toDomain);
    }
}
