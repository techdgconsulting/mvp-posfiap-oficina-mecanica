package br.com.oficina.application.service;

import br.com.oficina.application.dto.*;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.domain.atendimento.cliente.ClienteRepository;
import br.com.oficina.domain.atendimento.veiculo.VeiculoRepository;
import br.com.oficina.domain.encerramento.Encerramento;
import br.com.oficina.domain.encerramento.EncerramentoRepository;
import br.com.oficina.domain.entrega.Entrega;
import br.com.oficina.domain.entrega.EntregaRepository;
import br.com.oficina.domain.estoque.PecaRepository;
import br.com.oficina.domain.execucao.Execucao;
import br.com.oficina.domain.execucao.ExecucaoRepository;
import br.com.oficina.domain.execucao.StatusExecucao;
import br.com.oficina.domain.financeiro.MetodoPagamento;
import br.com.oficina.domain.financeiro.Pagamento;
import br.com.oficina.domain.financeiro.PagamentoGateway;
import br.com.oficina.domain.financeiro.PagamentoRepository;
import br.com.oficina.domain.orcamento.Orcamento;
import br.com.oficina.domain.orcamento.OrcamentoRepository;
import br.com.oficina.domain.ordemservico.*;
import br.com.oficina.domain.servico.ServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdemDeServicoService {

    private final OrdemDeServicoRepository osRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final ServicoRepository servicoRepository;
    private final PecaRepository pecaRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final ExecucaoRepository execucaoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final EntregaRepository entregaRepository;
    private final EncerramentoRepository encerramentoRepository;
    private final PagamentoGateway pagamentoGateway;

    @Transactional
    public OrdemServicoResponse criarOS(CriarOrdemServicoRequest req) {
        var cliente = clienteRepository.buscarPorId(req.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
        var veiculo = veiculoRepository.buscarPorId(req.veiculoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado"));

        // captura atendente autenticado
        org.springframework.security.core.Authentication auth =
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String atendenteNome = (auth != null) ? auth.getName() : "desconhecido";

        // cria a OS
        var os = OrdemDeServico.criar(cliente, veiculo);
        os.atribuirAtendente(atendenteNome);

        // adiciona itens se vieram no request
        if (req.itens() != null && !req.itens().isEmpty()) {
            for (var itemReq : req.itens()) {
                var item = montarItem(itemReq);
                os.adicionarItem(item);
            }
        }

        os = osRepository.salvar(os);

        // gera número legível da OS: OS-AAAA-NNNNN
        String numero = String.format("OS-%d-%05d", os.getDataCriacao().getYear(), os.getId());
        os.atribuirNumero(numero);
        os = osRepository.salvar(os);

        // já cria a execução vinculada
        var execucao = Execucao.criar(os.getId());
        execucaoRepository.salvar(execucao);

        log.info("OS {} criada para cliente {} - veículo {}", os.getNumero(), cliente.getNome(), veiculo.getPlaca().getValor());
        return toResponse(os);
    }

    @Transactional(readOnly = true)
    public OrdemServicoResponse buscarPorId(Long id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public OrdemServicoResponse buscarPorNumero(String numero) {
        var os = osRepository.buscarPorNumero(numero)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ordem de serviço não encontrada: " + numero));
        return toResponse(os);
    }

    @Transactional(readOnly = true)
    public List<OrdemServicoResponse> listarTodas() {
        return osRepository.listarTodas().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<OrdemServicoResponse> listarPorCliente(Long clienteId) {
        return osRepository.listarPorCliente(clienteId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<OrdemServicoResponse> listarPorStatus(StatusOS status) {
        return osRepository.listarPorStatus(status).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public String consultarStatus(Long id) {
        return findById(id).getStatus().name();
    }

    /**
     * Mecânico inicia o diagnóstico: OS avança de RECEBIDA → EM_DIAGNOSTICO.
     * Deve ser chamado antes de gerarOrcamento.
     */
    @Transactional
    public OrdemServicoResponse iniciarDiagnostico(Long osId) {
        var os = findById(osId);
        os.avancarParaDiagnostico();
        osRepository.salvar(os);

        // sincroniza BC Execução: AGUARDANDO → DIAGNOSTICO + registra data de início do diagnóstico
        execucaoRepository.buscarPorOrdemDeServico(osId).ifPresent(exec -> {
            org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            String mecanicoNome = (auth != null) ? auth.getName() : "desconhecido";
            exec.iniciarDiagnostico(mecanicoNome);
            execucaoRepository.salvar(exec);
        });

        return toResponse(os);
    }

    /**
     * Gera orçamento a partir dos itens da OS.
     * Exige que a OS esteja em EM_DIAGNOSTICO (fluxo normal)
     * ou EM_EXECUCAO (novo problema identificado durante execução).
     */
    @Transactional
    public OrcamentoResponse gerarOrcamento(Long osId) {
        var os = findById(osId);

        if (os.getItens().isEmpty()) {
            throw new NegocioException("OS não tem itens para gerar orçamento");
        }

        BigDecimal valorTotal = os.calcularValorTotal();
        var orcamento = Orcamento.gerar(osId, valorTotal);
        orcamento.enviar();
        orcamento = orcamentoRepository.salvar(orcamento);

        // aguardarAprovacao() valida: aceita EM_DIAGNOSTICO ou EM_EXECUCAO
        os.aguardarAprovacao();
        osRepository.salvar(os);

        return toOrcamentoResponse(orcamento);
    }

    @Transactional
    public OrdemServicoResponse aprovarOrcamento(Long osId) {
        var os = findById(osId);
        var orcamento = orcamentoRepository.buscarAtivoByOrdemDeServico(osId)
                .orElseThrow(() -> new NegocioException("Nenhum orçamento ativo encontrado para esta OS"));

        orcamento.aprovar();
        orcamentoRepository.salvar(orcamento);

        os.aprovarEIniciarExecucao();

        // baixa estoque apenas de peças ainda não reduzidas (evita dupla redução no "novo problema")
        baixarEstoquePecas(os);
        osRepository.salvar(os); // persiste OS + itens com estoqueReduzido=true via cascade

        // sincroniza Execucao: DIAGNOSTICO → EM_ANDAMENTO (mecânico começa a executar)
        // no "novo problema" a Execucao já está EM_ANDAMENTO — nenhuma ação necessária
        execucaoRepository.buscarPorOrdemDeServico(osId).ifPresent(exec -> {
            if (exec.getStatus() == StatusExecucao.EM_DIAGNOSTICO) {
                exec.iniciarServico();
                execucaoRepository.salvar(exec);
            }
        });

        log.info("Orçamento da OS #{} aprovado. Valor: R$ {}", osId, orcamento.getValorTotal());
        return toResponse(os);
    }

    @Transactional
    public OrdemServicoResponse rejeitarOrcamento(Long osId) {
        var os = findById(osId);
        var orcamento = orcamentoRepository.buscarAtivoByOrdemDeServico(osId)
                .orElseThrow(() -> new NegocioException("Nenhum orçamento ativo para rejeitar"));

        orcamento.rejeitar();
        orcamentoRepository.salvar(orcamento);

        // cliente rejeitou, cancela a OS
        os.cancelar();
        osRepository.salvar(os);

        return toResponse(os);
    }

    @Transactional
    public OrdemServicoResponse finalizarServico(Long osId) {
        var os = findById(osId);
        os.finalizar();
        osRepository.salvar(os);

        // finaliza execução também (auto-avança de AGUARDANDO/DIAGNOSTICO → EM_ANDAMENTO → SERVICO_FINALIZADO)
        execucaoRepository.buscarPorOrdemDeServico(osId).ifPresent(exec -> {
            if (exec.getStatus() == StatusExecucao.AGUARDANDO
                    || exec.getStatus() == StatusExecucao.EM_DIAGNOSTICO) {
                exec.iniciarServico();
            }
            exec.finalizarServico();
            execucaoRepository.salvar(exec);
        });

        return toResponse(os);
    }

    @Transactional
    public OrdemServicoResponse registrarPagamento(Long osId, String metodoPagamento) {
        var os = findById(osId);
        if (os.getStatus() != StatusOS.FINALIZADA) {
            throw new NegocioException("OS precisa estar FINALIZADA para registrar pagamento");
        }

        var valorTotal = os.calcularValorTotal();
        var metodo = MetodoPagamento.valueOf(metodoPagamento.toUpperCase());
        var pagamento = Pagamento.builder()
                .ordemDeServicoId(osId)
                .valor(valorTotal)
                .metodo(metodo)
                .build();

        // chama gateway externo (mock) — port/adapter DDD
        var gatewayResp = pagamentoGateway.processar(
                new PagamentoGateway.GatewayRequest(osId, valorTotal, metodo));

        if (!gatewayResp.aprovado()) {
            pagamento.recusar(gatewayResp.transactionId(), gatewayResp.mensagem());
            pagamentoRepository.salvar(pagamento);
            log.warn("Pagamento RECUSADO para OS #{} via {} (tx={}): {}",
                    osId, metodo, gatewayResp.transactionId(), gatewayResp.mensagem());
            throw new NegocioException("Pagamento recusado pelo gateway: " + gatewayResp.mensagem());
        }

        pagamento.aprovar(gatewayResp.transactionId(), gatewayResp.mensagem());
        pagamentoRepository.salvar(pagamento);

        // pagamento aprovado: OS aguarda retirada física do veículo pelo cliente
        os.aguardarRetirada();
        osRepository.salvar(os);

        log.info("Pagamento aprovado para OS #{} via {} (tx={}). Aguardando retirada do veículo.",
                osId, metodo, gatewayResp.transactionId());
        return toResponse(os);
    }

    @Transactional
    public OrdemServicoResponse entregarVeiculo(Long osId) {
        var os = findById(osId);
        if (os.getStatus() != StatusOS.AGUARDANDO_RETIRADA) {
            throw new IllegalStateException("OS precisa estar AGUARDANDO_RETIRADA para entregar o veículo");
        }

        // registra entrega física do veículo
        var entrega = Entrega.criar(osId);
        entrega.liberarVeiculo();
        entrega.entregarVeiculo();
        entregaRepository.salvar(entrega);

        // OS → ENTREGUE
        os.entregar();
        osRepository.salvar(os);

        // encerramento formal da OS
        var encerramento = Encerramento.criar(osId);
        encerramento.encerrar();
        encerramentoRepository.salvar(encerramento);

        log.info("Veículo da OS #{} entregue ao cliente. OS encerrada.", osId);
        return toResponse(os);
    }

    /**
     * Retorna o tempo médio de execução das OS: dataCriacao → dataFinalizacao (trabalho mecânico concluído).
     */
    public double calcularTempoMedioExecucao() {
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

    /**
     * Retorna o tempo médio de atendimento por OS: dataCriacao → dataEntrega (veículo retirado pelo cliente).
     * Representa o ciclo completo do atendimento.
     */
    public double calcularTempoMedioAtendimento() {
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

    /**
     * Formata uma duração em minutos: "2d 3h 15min", "4h 30min", "45min".
     */
    private static String formatarDuracao(long minutos) {
        if (minutos <= 0) return "0min";
        long dias  = minutos / (60 * 24);
        long horas = (minutos % (60 * 24)) / 60;
        long min   = minutos % 60;
        var sb = new StringBuilder();
        if (dias  > 0) sb.append(dias).append("d ");
        if (horas > 0) sb.append(horas).append("h ");
        if (min   > 0) sb.append(min).append("min");
        return sb.toString().trim();
    }

    /**
     * Retorna o breakdown temporal de uma OS específica com durações formatadas.
     */
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> calcularMetricasOS(Long osId) {
        var os = findById(osId);
        var resp = new java.util.LinkedHashMap<String, Object>();
        resp.put("numero", os.getNumero());
        resp.put("status", os.getStatus().name());
        resp.put("dataCriacao", os.getDataCriacao());
        resp.put("dataFinalizacao", os.getDataFinalizacao());
        resp.put("tempoExecucao",
                (os.getDataCriacao() != null && os.getDataFinalizacao() != null)
                ? formatarDuracao(Duration.between(os.getDataCriacao(), os.getDataFinalizacao()).toMinutes())
                : null);

        var entregaOpt = entregaRepository.buscarPorOrdemDeServico(osId);
        var dataEntrega = entregaOpt.map(Entrega::getDataEntrega).orElse(null);
        resp.put("dataEntrega", dataEntrega);
        resp.put("tempoAtendimento",
                (os.getDataCriacao() != null && dataEntrega != null)
                ? formatarDuracao(Duration.between(os.getDataCriacao(), dataEntrega).toMinutes())
                : null);
        return resp;
    }

    // -- métodos auxiliares --

    /**
     * Adiciona itens a uma OS existente.
     * Permitido nos status: RECEBIDA, EM_DIAGNOSTICO (itens antes do orçamento) e
     * EM_EXECUCAO (novo problema identificado durante execução).
     */
    @Transactional
    public OrdemServicoResponse adicionarItens(Long osId, java.util.List<ItemOSRequest> itens) {
        var os = findById(osId);
        var statusPermitidos = java.util.Set.of(
                StatusOS.RECEBIDA, StatusOS.EM_DIAGNOSTICO, StatusOS.EM_EXECUCAO);
        if (!statusPermitidos.contains(os.getStatus())) {
            throw new NegocioException(
                "Não é possível adicionar itens no status: " + os.getStatus()
                + ". Permitido em: RECEBIDA, EM_DIAGNOSTICO ou EM_EXECUCAO");
        }
        for (var req : itens) {
            var item = montarItem(req);
            os.adicionarItem(item);
        }
        osRepository.salvar(os);
        return toResponse(os);
    }

    private void baixarEstoquePecas(OrdemDeServico os) {
        for (var item : os.getItens()) {
            if (item.getTipo() == TipoItem.PECA
                    && item.getReferenciaId() != null
                    && !item.isEstoqueReduzido()) {
                var peca = pecaRepository.buscarPorId(item.getReferenciaId())
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Peça não encontrada: " + item.getReferenciaId()));
                peca.baixarEstoque(item.getQuantidade());
                pecaRepository.salvar(peca);
                item.marcarEstoqueReduzido();
            }
        }
    }

    private ItemOS montarItem(ItemOSRequest req) {
        TipoItem tipo = TipoItem.valueOf(req.tipo().toUpperCase());

        String descricao;
        BigDecimal valorUnitario;

        if (tipo == TipoItem.SERVICO) {
            var servico = servicoRepository.buscarPorId(req.referenciaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado: " + req.referenciaId()));
            descricao = servico.getNome();
            valorUnitario = servico.getValorUnitario();
        } else {
            var peca = pecaRepository.buscarPorId(req.referenciaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Peça não encontrada: " + req.referenciaId()));
            // só verifica disponibilidade, baixa quando aprovar
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

    private OrdemDeServico findById(Long id) {
        return osRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ordem de serviço não encontrada: " + id));
    }

    private OrdemServicoResponse toResponse(OrdemDeServico os) {
        var itensResp = os.getItens().stream()
                .map(i -> new OrdemServicoResponse.ItemOSResponse(
                        i.getId(), i.getTipo().name(), i.getDescricao(),
                        i.getQuantidade(), i.getValorUnitario(), i.calcularSubtotal()
                )).toList();

        String veiculoDesc = os.getVeiculo().getMarca() + " " + os.getVeiculo().getModelo() + " " + os.getVeiculo().getAno();

        String mecanicoNome = execucaoRepository.buscarPorOrdemDeServico(os.getId())
                .map(Execucao::getMecanicoNome)
                .orElse(null);

        return new OrdemServicoResponse(
                os.getId(), os.getNumero(), os.getStatus().name(), os.getDataCriacao(), os.getDataFinalizacao(),
                os.getCliente().getNome(), os.getCliente().getDocumento().formatado(),
                os.getVeiculo().getPlaca().getValor(), veiculoDesc,
                itensResp, os.calcularValorTotal(), os.getAtendenteNome(), mecanicoNome
        );
    }

    private OrcamentoResponse toOrcamentoResponse(Orcamento o) {
        return new OrcamentoResponse(o.getId(), o.getOrdemDeServicoId(), o.getStatus().name(),
                o.getValorTotal(), o.getDataCriacao(), o.getDataValidade());
    }
}
