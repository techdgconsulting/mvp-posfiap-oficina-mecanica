package br.com.oficina.adapters.out.persistence;

import br.com.oficina.adapters.out.persistence.mapper.PagamentoPersistenceMapper;
import br.com.oficina.adapters.out.persistence.repository.SpringDataPagamentoRepository;
import br.com.oficina.application.port.out.PagamentoRepositoryPort;
import br.com.oficina.domain.model.Pagamento;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagamentoPersistenceAdapter implements PagamentoRepositoryPort {

    private final SpringDataPagamentoRepository repository;
    private final PagamentoPersistenceMapper mapper;

    @Override
    public Pagamento salvar(Pagamento pagamento) {
        return mapper.toDomain(repository.save(mapper.toEntity(pagamento)));
    }

    @Override
    public Optional<Pagamento> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Pagamento> buscarPorOrdemDeServico(Long ordemServicoId) {
        return repository.findByOrdemDeServicoId(ordemServicoId).map(mapper::toDomain);
    }
}
