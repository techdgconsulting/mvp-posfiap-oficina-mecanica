package br.com.oficina.application.service;

import br.com.oficina.application.dto.PecaRequest;
import br.com.oficina.application.dto.PecaResponse;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.domain.estoque.Peca;
import br.com.oficina.domain.estoque.PecaRepository;
import br.com.oficina.domain.estoque.vo.Quantidade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PecaService {

    private final PecaRepository pecaRepository;

    @Transactional
    public PecaResponse criar(PecaRequest req) {
        var builder = Peca.builder()
                .nome(req.nome())
                .descricao(req.descricao())
                .quantidadeEstoque(new Quantidade(req.quantidadeEstoque()))
                .valorUnitario(req.valorUnitario());
        if (req.estoqueMinimo() != null) {
            builder.estoqueMinimo(req.estoqueMinimo());
        }
        return toResponse(pecaRepository.salvar(builder.build()));
    }

    public PecaResponse buscarPorId(Long id) {
        return toResponse(findById(id));
    }

    public List<PecaResponse> listarTodas() {
        return pecaRepository.listarTodas().stream().map(this::toResponse).toList();
    }

    public List<PecaResponse> listarEstoqueBaixo() {
        return pecaRepository.listarComEstoqueBaixo().stream().map(this::toResponse).toList();
    }

    @Transactional
    public PecaResponse atualizar(Long id, PecaRequest req) {
        var peca = findById(id);
        peca.atualizar(req.nome(), req.descricao(), req.valorUnitario(), req.estoqueMinimo());
        if (req.quantidadeEstoque() != null) {
            peca.setQuantidadeEstoque(new Quantidade(req.quantidadeEstoque()));
        }
        return toResponse(pecaRepository.salvar(peca));
    }

    @Transactional
    public void excluir(Long id) {
        findById(id);
        pecaRepository.excluir(id);
    }

    @Transactional
    public PecaResponse reporEstoque(Long id, int quantidade) {
        var peca = findById(id);
        peca.reporEstoque(quantidade);
        return toResponse(pecaRepository.salvar(peca));
    }

    private Peca findById(Long id) {
        return pecaRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Peça não encontrada: " + id));
    }

    private PecaResponse toResponse(Peca p) {
        return new PecaResponse(p.getId(), p.getNome(), p.getDescricao(), p.getQuantidadeEstoqueValor(), p.getValorUnitario(), p.getEstoqueMinimo(), p.estaComEstoqueBaixo());
    }
}
