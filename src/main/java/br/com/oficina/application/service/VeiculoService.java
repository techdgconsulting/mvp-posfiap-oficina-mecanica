package br.com.oficina.application.service;

import br.com.oficina.application.dto.VeiculoRequest;
import br.com.oficina.application.dto.VeiculoResponse;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.domain.atendimento.cliente.ClienteRepository;
import br.com.oficina.domain.atendimento.veiculo.Veiculo;
import br.com.oficina.domain.atendimento.veiculo.VeiculoRepository;
import br.com.oficina.domain.atendimento.veiculo.vo.Placa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public VeiculoResponse criar(VeiculoRequest req) {
        var cliente = clienteRepository.buscarPorId(req.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado: " + req.clienteId()));

        String placaFormatada = new Placa(req.placa()).getValor();
        if (veiculoRepository.buscarPorPlaca(placaFormatada).isPresent()) {
            throw new NegocioException("Já existe um veículo cadastrado com a placa: " + placaFormatada);
        }

        var veiculo = Veiculo.builder()
                .placa(new Placa(req.placa()))
                .marca(req.marca())
                .modelo(req.modelo())
                .ano(req.ano())
                .cliente(cliente)
                .build();

        veiculo = veiculoRepository.salvar(veiculo);
        return toResponse(veiculo);
    }

    public VeiculoResponse buscarPorId(Long id) {
        return toResponse(findById(id));
    }

    public List<VeiculoResponse> listarTodos() {
        return veiculoRepository.listarTodos().stream().map(this::toResponse).toList();
    }

    public List<VeiculoResponse> listarPorCliente(Long clienteId) {
        return veiculoRepository.listarPorCliente(clienteId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public VeiculoResponse atualizar(Long id, VeiculoRequest req) {
        var veiculo = findById(id);
        veiculo.atualizar(req.marca(), req.modelo(), req.ano());
        return toResponse(veiculoRepository.salvar(veiculo));
    }

    @Transactional
    public void excluir(Long id) {
        findById(id); // garante que existe
        veiculoRepository.excluir(id);
    }

    private Veiculo findById(Long id) {
        return veiculoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado: " + id));
    }

    private VeiculoResponse toResponse(Veiculo v) {
        return new VeiculoResponse(
            v.getId(),
            v.getPlaca().getValor(),
            v.getMarca(),
            v.getModelo(),
            v.getAno(),
            v.getCliente().getId(),
            v.getCliente().getNome()
        );
    }
}
