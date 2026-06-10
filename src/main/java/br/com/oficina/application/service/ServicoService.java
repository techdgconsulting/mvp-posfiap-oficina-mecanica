package br.com.oficina.application.service;

import br.com.oficina.application.dto.ServicoRequest;
import br.com.oficina.application.dto.ServicoResponse;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.domain.servico.Servico;
import br.com.oficina.domain.servico.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;

    @Transactional
    public ServicoResponse criar(ServicoRequest req) {
        var servico = new Servico(req.nome(), req.descricao(), req.valorUnitario(), req.tempoEstimadoMinutos());
        servico = servicoRepository.salvar(servico);
        return toResponse(servico);
    }

    public ServicoResponse buscarPorId(Long id) {
        return toResponse(findById(id));
    }

    public List<ServicoResponse> listarTodos() {
        return servicoRepository.listarTodos().stream().map(this::toResponse).toList();
    }

    @Transactional
    public ServicoResponse atualizar(Long id, ServicoRequest req) {
        var servico = findById(id);
        servico.atualizar(req.nome(), req.descricao(), req.valorUnitario(), req.tempoEstimadoMinutos());
        return toResponse(servicoRepository.salvar(servico));
    }

    @Transactional
    public void excluir(Long id) {
        findById(id);
        servicoRepository.excluir(id);
    }

    private Servico findById(Long id) {
        return servicoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado com id " + id));
    }

    private ServicoResponse toResponse(Servico s) {
        return new ServicoResponse(s.getId(), s.getNome(), s.getDescricao(), s.getValorUnitario(), s.getTempoEstimadoMinutos());
    }
}
