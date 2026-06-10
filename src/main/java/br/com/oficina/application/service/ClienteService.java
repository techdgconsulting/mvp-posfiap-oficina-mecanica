package br.com.oficina.application.service;

import br.com.oficina.application.dto.ClienteRequest;
import br.com.oficina.application.dto.ClienteResponse;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.domain.atendimento.cliente.Cliente;
import br.com.oficina.domain.atendimento.cliente.ClienteRepository;
import br.com.oficina.domain.atendimento.cliente.vo.CpfCnpj;
import br.com.oficina.domain.atendimento.veiculo.VeiculoRepository;
import br.com.oficina.domain.ordemservico.OrdemDeServicoRepository;
import br.com.oficina.infrastructure.client.ViaCepClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final OrdemDeServicoRepository ordemDeServicoRepository;
    private final ViaCepClient viaCepClient;

    @Transactional
    public ClienteResponse criar(ClienteRequest req) {
        // verifica duplicidade
        if (clienteRepository.existePorDocumento(new CpfCnpj(req.documento()).getValor())) {
            throw new NegocioException("Já existe um cliente com esse documento");
        }

        var cliente = Cliente.builder()
                .documento(new CpfCnpj(req.documento()))
                .nome(req.nome())
                .telefone(req.telefone())
                .email(req.email())
                .build();

        // busca endereço via CEP se informado
        if (req.cep() != null && !req.cep().isBlank()) {
            viaCepClient.buscarPorCep(req.cep()).ifPresent(endereco ->
                cliente.preencherEndereco(
                    endereco.cep(),
                    endereco.logradouro(),
                    endereco.bairro(),
                    endereco.localidade(),
                    endereco.uf()
                )
            );
        }

        var salvo = clienteRepository.salvar(cliente);
        return toResponse(salvo);
    }

    public ClienteResponse buscarPorId(Long id) {
        var cliente = clienteRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado: " + id));
        return toResponse(cliente);
    }

    public ClienteResponse buscarPorDocumento(String documento) {
        String doc = documento.replaceAll("[^\\d]", "");
        var cliente = clienteRepository.buscarPorDocumento(doc)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado com documento: " + documento));
        return toResponse(cliente);
    }

    public List<ClienteResponse> listarTodos() {
        return clienteRepository.listarTodos().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest req) {
        var cliente = clienteRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado: " + id));

        cliente.atualizar(req.nome(), req.telefone(), req.email());
        cliente = clienteRepository.salvar(cliente);
        return toResponse(cliente);
    }

    @Transactional
    public void excluir(Long id) {
        if (clienteRepository.buscarPorId(id).isEmpty()) {
            throw new RecursoNaoEncontradoException("Cliente não encontrado: " + id);
        }
        if (!veiculoRepository.listarPorCliente(id).isEmpty()) {
            throw new NegocioException("Não é possível excluir cliente com veículos vinculados");
        }
        if (!ordemDeServicoRepository.listarPorCliente(id).isEmpty()) {
            throw new NegocioException("Não é possível excluir cliente com ordens de serviço vinculadas");
        }
        clienteRepository.excluir(id);
    }

    private ClienteResponse toResponse(Cliente c) {
        return new ClienteResponse(
            c.getId(),
            c.getDocumento().formatado(),
            c.getDocumento().getTipo().name(),
            c.getNome(),
            c.getTelefone(),
            c.getEmail(),
            c.getCep(),
            c.getLogradouro(),
            c.getBairro(),
            c.getCidade(),
            c.getUf()
        );
    }
}
