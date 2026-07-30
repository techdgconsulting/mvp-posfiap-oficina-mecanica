package br.com.oficina.application.usecase;

import br.com.oficina.application.command.CriarOrdemServicoCompletaCommand;
import br.com.oficina.application.command.ItemOSCommand;
import br.com.oficina.application.command.NotificarStatusOrdemServicoCommand;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.CriarOrdemServicoCompletaInputPort;
import br.com.oficina.application.port.in.NotificarStatusOrdemServicoInputPort;
import br.com.oficina.application.port.out.BuscarEnderecoPorCepPort;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.port.out.ExecucaoRepositoryPort;
import br.com.oficina.application.port.out.OrdemDeServicoRepositoryPort;
import br.com.oficina.application.port.out.PecaRepositoryPort;
import br.com.oficina.application.port.out.ServicoRepositoryPort;
import br.com.oficina.application.port.out.VeiculoRepositoryPort;
import br.com.oficina.application.query.ItemOSResult;
import br.com.oficina.application.query.OrdemServicoResult;
import br.com.oficina.domain.model.Cliente;
import br.com.oficina.domain.model.Execucao;
import br.com.oficina.domain.model.ItemOS;
import br.com.oficina.domain.model.OrdemDeServico;
import br.com.oficina.domain.model.Veiculo;
import br.com.oficina.domain.valueobject.CpfCnpj;
import br.com.oficina.domain.valueobject.Placa;
import br.com.oficina.domain.valueobject.TipoItem;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class CriarOrdemServicoCompletaUseCase implements CriarOrdemServicoCompletaInputPort {

    private final OrdemDeServicoRepositoryPort osRepository;
    private final ClienteRepositoryPort clienteRepository;
    private final VeiculoRepositoryPort veiculoRepository;
    private final ServicoRepositoryPort servicoRepository;
    private final PecaRepositoryPort pecaRepository;
    private final ExecucaoRepositoryPort execucaoRepository;
    private final BuscarEnderecoPorCepPort buscarEnderecoPorCepPort;
    private final NotificarStatusOrdemServicoInputPort notificarStatusOrdemServicoInputPort;

    @Override
    @Transactional
    public OrdemServicoResult execute(CriarOrdemServicoCompletaCommand command) {
        var cliente = obterOuCriarCliente(command);
        var veiculo = obterOuCriarVeiculo(command, cliente);

        var os = OrdemDeServico.criar(cliente, veiculo);
        os.atribuirAtendente(command.atendenteNome());
        for (var item : itens(command)) {
            os.adicionarItem(montarItem(item));
        }

        os = osRepository.salvar(os);
        os.atribuirNumero(String.format("OS-%d-%05d", os.getDataCriacao().getYear(), os.getId()));
        os = osRepository.salvar(os);

        execucaoRepository.salvar(Execucao.criar(os.getId()));
        notificarStatus(os, cliente);
        return toResult(os);
    }

    private Cliente obterOuCriarCliente(CriarOrdemServicoCompletaCommand command) {
        var documento = new CpfCnpj(command.documento());
        return clienteRepository.buscarPorDocumento(documento.getValor())
                .orElseGet(() -> clienteRepository.salvar(novoCliente(command, documento)));
    }

    private Cliente novoCliente(CriarOrdemServicoCompletaCommand command, CpfCnpj documento) {
        var cliente = new Cliente(documento, command.nome(), command.telefone(), command.email());
        if (temEnderecoInformado(command)) {
            cliente.preencherEndereco(command.cep(), command.logradouro(), command.bairro(), command.cidade(), command.uf());
        } else if (command.cep() != null && !command.cep().isBlank()) {
            buscarEnderecoPorCepPort.buscarPorCep(command.cep()).ifPresent(endereco ->
                cliente.preencherEndereco(
                    endereco.cep(),
                    endereco.logradouro(),
                    endereco.bairro(),
                    endereco.cidade(),
                    endereco.uf()
                )
            );
        }
        return cliente;
    }

    private boolean temEnderecoInformado(CriarOrdemServicoCompletaCommand command) {
        return notBlank(command.logradouro())
                || notBlank(command.bairro())
                || notBlank(command.cidade())
                || notBlank(command.uf());
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    private Veiculo obterOuCriarVeiculo(CriarOrdemServicoCompletaCommand command, Cliente cliente) {
        var placa = new Placa(command.placa());
        var existente = veiculoRepository.buscarPorPlaca(placa.getValor());
        if (existente.isPresent()) {
            var veiculo = existente.get();
            if (!cliente.getId().equals(veiculo.getClienteId())) {
                throw new NegocioException("Veiculo ja cadastrado para outro cliente: " + placa.getValor());
            }
            return veiculo;
        }
        return veiculoRepository.salvar(new Veiculo(placa, command.marca(), command.modelo(), command.ano(), cliente));
    }

    private List<ItemOSCommand> itens(CriarOrdemServicoCompletaCommand command) {
        return command.itens() != null ? command.itens() : List.of();
    }

    private ItemOS montarItem(ItemOSCommand req) {
        TipoItem tipo = TipoItem.valueOf(req.tipo().toUpperCase());
        String descricao;
        BigDecimal valorUnitario;

        if (tipo == TipoItem.SERVICO) {
            var servico = servicoRepository.buscarPorId(req.referenciaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Servico nao encontrado: " + req.referenciaId()));
            descricao = servico.getNome();
            valorUnitario = servico.getValorUnitario();
        } else {
            var peca = pecaRepository.buscarPorId(req.referenciaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Peca nao encontrada: " + req.referenciaId()));
            peca.verificarDisponibilidade(req.quantidade());
            descricao = peca.getNome();
            valorUnitario = peca.getValorUnitario();
        }

        return ItemOS.builder()
                .tipo(tipo)
                .descricao(descricao)
                .quantidade(req.quantidade())
                .valorUnitario(valorUnitario)
                .referenciaId(req.referenciaId())
                .build();
    }

    private OrdemServicoResult toResult(OrdemDeServico os) {
        var cliente = clienteRepository.buscarPorId(os.getClienteId()).orElse(os.getCliente());
        var veiculo = veiculoRepository.buscarPorId(os.getVeiculoId()).orElse(os.getVeiculo());
        var itensResp = os.getItens().stream()
                .map(i -> new ItemOSResult(
                        i.getId(), i.getTipo().name(), i.getDescricao(),
                        i.getQuantidade(), i.getValorUnitario(), i.calcularSubtotal()
                )).toList();
        String veiculoDesc = veiculo != null
                ? veiculo.getMarca() + " " + veiculo.getModelo() + " " + veiculo.getAno()
                : null;

        return new OrdemServicoResult(
                os.getId(), os.getNumero(), os.getStatus().name(), os.getDataCriacao(), os.getDataFinalizacao(),
                cliente != null ? cliente.getNome() : null,
                cliente != null ? cliente.getDocumento().formatado() : null,
                veiculo != null ? veiculo.getPlaca().getValor() : null,
                veiculoDesc,
                itensResp,
                os.calcularValorTotal(),
                os.getAtendenteNome(),
                null
        );
    }

    private void notificarStatus(OrdemDeServico os, Cliente cliente) {
        try {
            notificarStatusOrdemServicoInputPort.execute(new NotificarStatusOrdemServicoCommand(
                    os.getId(),
                    os.getNumero(),
                    os.getStatus().name(),
                    cliente.getNome(),
                    cliente.getEmail()));
        } catch (Exception ex) {
            log.warn("NOTIFICACAO_STATUS_OS_FALHOU osId={} numero={} status={} erro={}",
                    os.getId(), os.getNumero(), os.getStatus(), ex.getMessage());
        }
    }
}
