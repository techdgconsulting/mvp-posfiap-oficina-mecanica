package br.com.oficina.application.usecase;

import br.com.oficina.application.command.AdicionarItemOSCommand;
import br.com.oficina.application.command.AprovarOrcamentoCommand;
import br.com.oficina.application.command.CriarOrdemServicoCommand;
import br.com.oficina.application.command.EntregarVeiculoCommand;
import br.com.oficina.application.command.FinalizarServicoCommand;
import br.com.oficina.application.command.GerarOrcamentoCommand;
import br.com.oficina.application.command.IniciarDiagnosticoCommand;
import br.com.oficina.application.command.ItemOSCommand;
import br.com.oficina.application.command.NotificarStatusOrdemServicoCommand;
import br.com.oficina.application.command.RegistrarPagamentoCommand;
import br.com.oficina.application.command.RejeitarOrcamentoCommand;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.AdicionarItensOrdemServicoInputPort;
import br.com.oficina.application.port.in.AprovarOrcamentoInputPort;
import br.com.oficina.application.port.in.BuscarOrdemServicoPorIdInputPort;
import br.com.oficina.application.port.in.BuscarOrdemServicoPorNumeroInputPort;
import br.com.oficina.application.port.in.CalcularMetricasOSInputPort;
import br.com.oficina.application.port.in.CalcularTempoMedioOSInputPort;
import br.com.oficina.application.port.in.ConsultarStatusOrdemServicoInputPort;
import br.com.oficina.application.port.in.CriarOrdemServicoInputPort;
import br.com.oficina.application.port.in.EntregarVeiculoInputPort;
import br.com.oficina.application.port.in.FinalizarServicoInputPort;
import br.com.oficina.application.port.in.GerarOrcamentoInputPort;
import br.com.oficina.application.port.in.IniciarDiagnosticoInputPort;
import br.com.oficina.application.port.in.ListarFilaOrdensServicoInputPort;
import br.com.oficina.application.port.in.ListarOrdensServicoInputPort;
import br.com.oficina.application.port.in.ListarOrdensServicoPorClienteInputPort;
import br.com.oficina.application.port.in.ListarOrdensServicoPorStatusInputPort;
import br.com.oficina.application.port.in.NotificarStatusOrdemServicoInputPort;
import br.com.oficina.application.port.in.RegistrarPagamentoInputPort;
import br.com.oficina.application.port.in.RejeitarOrcamentoInputPort;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.port.out.EncerramentoRepositoryPort;
import br.com.oficina.application.port.out.EntregaRepositoryPort;
import br.com.oficina.application.port.out.ExecucaoRepositoryPort;
import br.com.oficina.application.port.out.OrcamentoRepositoryPort;
import br.com.oficina.application.port.out.OrdemDeServicoRepositoryPort;
import br.com.oficina.application.port.out.PagamentoGatewayPort;
import br.com.oficina.application.port.out.PagamentoRepositoryPort;
import br.com.oficina.application.port.out.PecaRepositoryPort;
import br.com.oficina.application.port.out.ServicoRepositoryPort;
import br.com.oficina.application.port.out.VeiculoRepositoryPort;
import br.com.oficina.application.query.ItemOSResult;
import br.com.oficina.application.query.MetricasOSResult;
import br.com.oficina.application.query.OrcamentoResult;
import br.com.oficina.application.query.OrdemServicoResult;
import br.com.oficina.application.query.TempoMedioOSResult;
import br.com.oficina.domain.model.Encerramento;
import br.com.oficina.domain.model.Entrega;
import br.com.oficina.domain.model.Execucao;
import br.com.oficina.domain.model.ItemOS;
import br.com.oficina.domain.model.OrdemDeServico;
import br.com.oficina.domain.model.Orcamento;
import br.com.oficina.domain.model.Pagamento;
import br.com.oficina.domain.valueobject.MetodoPagamento;
import br.com.oficina.domain.valueobject.StatusExecucao;
import br.com.oficina.domain.valueobject.StatusOS;
import br.com.oficina.domain.valueobject.TipoItem;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OrdemDeServicoUseCase implements
        CriarOrdemServicoInputPort,
        BuscarOrdemServicoPorIdInputPort,
        BuscarOrdemServicoPorNumeroInputPort,
        ListarOrdensServicoInputPort,
        ListarOrdensServicoPorClienteInputPort,
        ListarOrdensServicoPorStatusInputPort,
        ConsultarStatusOrdemServicoInputPort,
        IniciarDiagnosticoInputPort,
        GerarOrcamentoInputPort,
        AprovarOrcamentoInputPort,
        RejeitarOrcamentoInputPort,
        FinalizarServicoInputPort,
        RegistrarPagamentoInputPort,
        EntregarVeiculoInputPort,
        AdicionarItensOrdemServicoInputPort,
        CalcularMetricasOSInputPort,
        CalcularTempoMedioOSInputPort,
        ListarFilaOrdensServicoInputPort {

    private final OrdemDeServicoRepositoryPort osRepository;
    private final ClienteRepositoryPort clienteRepository;
    private final VeiculoRepositoryPort veiculoRepository;
    private final ServicoRepositoryPort servicoRepository;
    private final PecaRepositoryPort pecaRepository;
    private final OrcamentoRepositoryPort orcamentoRepository;
    private final ExecucaoRepositoryPort execucaoRepository;
    private final PagamentoRepositoryPort pagamentoRepository;
    private final EntregaRepositoryPort entregaRepository;
    private final EncerramentoRepositoryPort encerramentoRepository;
    private final PagamentoGatewayPort pagamentoGateway;
    private final NotificarStatusOrdemServicoInputPort notificarStatusOrdemServicoInputPort;

    @Override
    public OrdemServicoResult execute(CriarOrdemServicoCommand command) {
        var cliente = clienteRepository.buscarPorId(command.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado"));
        var veiculo = veiculoRepository.buscarPorId(command.veiculoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veiculo nao encontrado"));

        var os = OrdemDeServico.criar(cliente, veiculo);
        os.atribuirAtendente(command.atendenteNome());

        if (command.itens() != null && !command.itens().isEmpty()) {
            for (var itemReq : command.itens()) {
                os.adicionarItem(montarItem(itemReq));
            }
        }

        os = osRepository.salvar(os);
        String numero = String.format("OS-%d-%05d", os.getDataCriacao().getYear(), os.getId());
        os.atribuirNumero(numero);
        os = osRepository.salvar(os);

        execucaoRepository.salvar(Execucao.criar(os.getId()));
        notificarStatus(os);
        return toResult(os);
    }

    @Override
    public OrdemServicoResult execute(Long id) {
        return toResult(findById(id));
    }

    @Override
    public OrdemServicoResult execute(String numero) {
        return toResult(osRepository.buscarPorNumero(numero)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ordem de servico nao encontrada: " + numero)));
    }

    @Override
    public List<OrdemServicoResult> execute() {
        return osRepository.listarTodas().stream().map(this::toResult).toList();
    }

    @Override
    public List<OrdemServicoResult> listarFilaOperacional() {
        return osRepository.listarFilaOperacional().stream().map(this::toResult).toList();
    }

    @Override
    public List<OrdemServicoResult> executeByCliente(Long clienteId) {
        return osRepository.listarPorCliente(clienteId).stream().map(this::toResult).toList();
    }

    @Override
    public List<OrdemServicoResult> execute(StatusOS status) {
        return osRepository.listarPorStatus(status).stream().map(this::toResult).toList();
    }

    @Override
    public String executeStatus(Long id) {
        return findById(id).getStatus().name();
    }

    @Override
    public OrdemServicoResult execute(IniciarDiagnosticoCommand command) {
        var os = findById(command.ordemServicoId());
        os.avancarParaDiagnostico();
        osRepository.salvar(os);

        execucaoRepository.buscarPorOrdemDeServico(command.ordemServicoId()).ifPresent(exec -> {
            exec.iniciarDiagnostico(command.mecanicoNome());
            execucaoRepository.salvar(exec);
        });

        notificarStatus(os);
        return toResult(os);
    }

    @Override
    public OrcamentoResult execute(GerarOrcamentoCommand command) {
        var os = findById(command.ordemServicoId());
        if (os.getItens().isEmpty()) {
            throw new NegocioException("OS nao tem itens para gerar orcamento");
        }
        if (os.getStatus() != StatusOS.EM_DIAGNOSTICO && os.getStatus() != StatusOS.EM_EXECUCAO) {
            throw new IllegalStateException(
                "Nao e possivel gerar orcamento no status: " + os.getStatus()
                + ". Permitido em: EM_DIAGNOSTICO ou EM_EXECUCAO");
        }

        BigDecimal valorTotal = os.calcularValorTotal();
        var orcamento = Orcamento.gerar(command.ordemServicoId(), valorTotal);
        orcamento.enviar();
        orcamento = orcamentoRepository.salvar(orcamento);

        os.aguardarAprovacao();
        osRepository.salvar(os);

        notificarStatus(os);
        return toOrcamentoResult(orcamento);
    }

    @Override
    public OrdemServicoResult execute(AprovarOrcamentoCommand command) {
        var os = findById(command.ordemServicoId());
        var orcamento = orcamentoRepository.buscarAtivoByOrdemDeServico(command.ordemServicoId())
                .orElseThrow(() -> new NegocioException("Nenhum orcamento ativo encontrado para esta OS"));

        orcamento.aprovar();
        orcamentoRepository.salvar(orcamento);

        try {
            os.aprovarEIniciarExecucao();
        } catch (IllegalStateException ex) {
            throw new NegocioException(ex.getMessage());
        }
        baixarEstoquePecas(os);
        osRepository.salvar(os);

        execucaoRepository.buscarPorOrdemDeServico(command.ordemServicoId()).ifPresent(exec -> {
            if (exec.getStatus() == StatusExecucao.EM_DIAGNOSTICO) {
                exec.iniciarServico();
                execucaoRepository.salvar(exec);
            }
        });

        notificarStatus(os);
        return toResult(os);
    }

    @Override
    public OrdemServicoResult execute(RejeitarOrcamentoCommand command) {
        var os = findById(command.ordemServicoId());
        var orcamento = orcamentoRepository.buscarAtivoByOrdemDeServico(command.ordemServicoId())
                .orElseThrow(() -> new NegocioException("Nenhum orcamento ativo para rejeitar"));

        orcamento.rejeitar();
        orcamentoRepository.salvar(orcamento);

        os.cancelar();
        osRepository.salvar(os);

        notificarStatus(os);
        return toResult(os);
    }

    @Override
    public OrdemServicoResult execute(FinalizarServicoCommand command) {
        var os = findById(command.ordemServicoId());
        os.finalizar();
        osRepository.salvar(os);

        execucaoRepository.buscarPorOrdemDeServico(command.ordemServicoId()).ifPresent(exec -> {
            if (exec.getStatus() == StatusExecucao.AGUARDANDO || exec.getStatus() == StatusExecucao.EM_DIAGNOSTICO) {
                exec.iniciarServico();
            }
            exec.finalizarServico();
            execucaoRepository.salvar(exec);
        });

        notificarStatus(os);
        return toResult(os);
    }

    @Override
    public OrdemServicoResult execute(RegistrarPagamentoCommand command) {
        var os = findById(command.ordemServicoId());
        if (os.getStatus() != StatusOS.FINALIZADA) {
            throw new NegocioException("OS precisa estar FINALIZADA para registrar pagamento");
        }

        var valorTotal = os.calcularValorTotal();
        var metodo = MetodoPagamento.valueOf(command.metodoPagamento().toUpperCase());
        var pagamento = Pagamento.builder()
                .ordemDeServicoId(command.ordemServicoId())
                .valor(valorTotal)
                .metodo(metodo)
                .build();

        var gatewayResp = pagamentoGateway.processar(
                new PagamentoGatewayPort.GatewayRequest(command.ordemServicoId(), valorTotal, metodo));

        if (!gatewayResp.aprovado()) {
            pagamento.recusar(gatewayResp.transactionId(), gatewayResp.mensagem());
            pagamentoRepository.salvar(pagamento);
            throw new NegocioException("Pagamento recusado pelo gateway: " + gatewayResp.mensagem());
        }

        pagamento.aprovar(gatewayResp.transactionId(), gatewayResp.mensagem());
        pagamentoRepository.salvar(pagamento);

        os.aguardarRetirada();
        osRepository.salvar(os);

        notificarStatus(os);
        return toResult(os);
    }

    @Override
    public OrdemServicoResult execute(EntregarVeiculoCommand command) {
        var os = findById(command.ordemServicoId());
        if (os.getStatus() != StatusOS.AGUARDANDO_RETIRADA) {
            throw new IllegalStateException("OS precisa estar AGUARDANDO_RETIRADA para entregar o veiculo");
        }

        var entrega = Entrega.criar(command.ordemServicoId());
        entrega.liberarVeiculo();
        entrega.entregarVeiculo();
        entregaRepository.salvar(entrega);

        os.entregar();
        osRepository.salvar(os);

        var encerramento = Encerramento.criar(command.ordemServicoId());
        encerramento.encerrar();
        encerramentoRepository.salvar(encerramento);

        notificarStatus(os);
        return toResult(os);
    }

    @Override
    public OrdemServicoResult execute(AdicionarItemOSCommand command) {
        var os = findById(command.ordemServicoId());
        var statusPermitidos = Set.of(StatusOS.RECEBIDA, StatusOS.EM_DIAGNOSTICO, StatusOS.EM_EXECUCAO);
        if (!statusPermitidos.contains(os.getStatus())) {
            throw new NegocioException(
                "Nao e possivel adicionar itens no status: " + os.getStatus()
                + ". Permitido em: RECEBIDA, EM_DIAGNOSTICO ou EM_EXECUCAO");
        }
        for (var req : command.itens()) {
            os.adicionarItem(montarItem(req));
        }
        osRepository.salvar(os);
        return toResult(os);
    }

    @Override
    public MetricasOSResult executeMetricas(Long ordemServicoId) {
        var os = findById(ordemServicoId);
        var entregaOpt = entregaRepository.buscarPorOrdemDeServico(ordemServicoId);
        var dataEntrega = entregaOpt.map(Entrega::getDataEntrega).orElse(null);
        return new MetricasOSResult(
            os.getNumero(),
            os.getStatus().name(),
            os.getDataCriacao(),
            os.getDataFinalizacao(),
            os.getDataCriacao() != null && os.getDataFinalizacao() != null
                    ? formatarDuracao(Duration.between(os.getDataCriacao(), os.getDataFinalizacao()).toMinutes())
                    : null,
            dataEntrega,
            os.getDataCriacao() != null && dataEntrega != null
                    ? formatarDuracao(Duration.between(os.getDataCriacao(), dataEntrega).toMinutes())
                    : null
        );
    }

    @Override
    public TempoMedioOSResult executeTempoMedio() {
        return new TempoMedioOSResult(calcularTempoMedioExecucao(), calcularTempoMedioAtendimento());
    }

    private OrdemDeServico findById(Long id) {
        return osRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ordem de servico nao encontrada: " + id));
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

    private void baixarEstoquePecas(OrdemDeServico os) {
        for (var item : os.getItens()) {
            if (item.getTipo() == TipoItem.PECA && item.getReferenciaId() != null && !item.isEstoqueReduzido()) {
                var peca = pecaRepository.buscarPorId(item.getReferenciaId())
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Peca nao encontrada: " + item.getReferenciaId()));
                peca.baixarEstoque(item.getQuantidade());
                pecaRepository.salvar(peca);
                item.marcarEstoqueReduzido();
            }
        }
    }

    private void notificarStatus(OrdemDeServico os) {
        try {
            var cliente = clienteRepository.buscarPorId(os.getClienteId()).orElse(os.getCliente());
            if (cliente == null) {
                log.warn("NOTIFICACAO_STATUS_OS_SEM_CLIENTE osId={} numero={} status={}",
                        os.getId(), os.getNumero(), os.getStatus());
                return;
            }

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
        String mecanicoNome = execucaoRepository.buscarPorOrdemDeServico(os.getId())
                .map(Execucao::getMecanicoNome)
                .orElse(null);

        return new OrdemServicoResult(
                os.getId(), os.getNumero(), os.getStatus().name(), os.getDataCriacao(), os.getDataFinalizacao(),
                cliente != null ? cliente.getNome() : null,
                cliente != null ? cliente.getDocumento().formatado() : null,
                veiculo != null ? veiculo.getPlaca().getValor() : null,
                veiculoDesc,
                itensResp,
                os.calcularValorTotal(),
                os.getAtendenteNome(),
                mecanicoNome
        );
    }

    private OrcamentoResult toOrcamentoResult(Orcamento o) {
        return new OrcamentoResult(o.getId(), o.getOrdemDeServicoId(), o.getStatus().name(),
                o.getValorTotal(), o.getDataCriacao(), o.getDataValidade());
    }

    private double calcularTempoMedioExecucao() {
        var entregues = osRepository.listarPorStatus(StatusOS.ENTREGUE);
        if (entregues.isEmpty()) return 0;
        long totalMinutos = 0;
        int count = 0;
        for (var os : entregues) {
            if (os.getDataCriacao() != null && os.getDataFinalizacao() != null) {
                totalMinutos += Duration.between(os.getDataCriacao(), os.getDataFinalizacao()).toMinutes();
                count++;
            }
        }
        return count > 0 ? (double) totalMinutos / count : 0;
    }

    private double calcularTempoMedioAtendimento() {
        var entregues = osRepository.listarPorStatus(StatusOS.ENTREGUE);
        if (entregues.isEmpty()) return 0;
        long totalMinutos = 0;
        int count = 0;
        for (var os : entregues) {
            if (os.getDataCriacao() == null) continue;
            var entrega = entregaRepository.buscarPorOrdemDeServico(os.getId());
            if (entrega.isPresent() && entrega.get().getDataEntrega() != null) {
                totalMinutos += Duration.between(os.getDataCriacao(), entrega.get().getDataEntrega()).toMinutes();
                count++;
            }
        }
        return count > 0 ? (double) totalMinutos / count : 0;
    }

    private static String formatarDuracao(long minutos) {
        if (minutos <= 0) return "0min";
        long dias = minutos / (60 * 24);
        long horas = (minutos % (60 * 24)) / 60;
        long min = minutos % 60;
        var sb = new StringBuilder();
        if (dias > 0) sb.append(dias).append("d ");
        if (horas > 0) sb.append(horas).append("h ");
        if (min > 0) sb.append(min).append("min");
        return sb.toString().trim();
    }
}
