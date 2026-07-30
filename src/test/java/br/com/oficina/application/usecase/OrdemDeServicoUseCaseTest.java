package br.com.oficina.application.usecase;

import br.com.oficina.application.command.AprovarOrcamentoCommand;
import br.com.oficina.application.command.CriarOrdemServicoCommand;
import br.com.oficina.application.command.FinalizarServicoCommand;
import br.com.oficina.application.command.GerarOrcamentoCommand;
import br.com.oficina.application.command.IniciarDiagnosticoCommand;
import br.com.oficina.application.command.ItemOSCommand;
import br.com.oficina.application.command.RegistrarPagamentoCommand;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
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
import br.com.oficina.application.port.in.NotificarStatusOrdemServicoInputPort;
import br.com.oficina.domain.model.Entrega;
import br.com.oficina.domain.model.Peca;
import br.com.oficina.domain.valueobject.Quantidade;
import br.com.oficina.domain.model.Execucao;
import br.com.oficina.domain.model.Cliente;
import br.com.oficina.domain.model.ItemOS;
import br.com.oficina.domain.model.OrdemDeServico;
import br.com.oficina.domain.model.Veiculo;
import br.com.oficina.domain.model.Orcamento;
import br.com.oficina.domain.model.Servico;
import br.com.oficina.domain.valueobject.CpfCnpj;
import br.com.oficina.domain.valueobject.MetodoPagamento;
import br.com.oficina.domain.valueobject.Placa;
import br.com.oficina.domain.valueobject.StatusOS;
import br.com.oficina.domain.valueobject.TipoItem;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Epic("Ordem de Servico")
@Feature("Use case de Ordens de Servico")
class OrdemDeServicoUseCaseTest {

    @Mock private OrdemDeServicoRepositoryPort osRepository;
    @Mock private ClienteRepositoryPort clienteRepository;
    @Mock private VeiculoRepositoryPort veiculoRepository;
    @Mock private ServicoRepositoryPort servicoRepository;
    @Mock private PecaRepositoryPort pecaRepository;
    @Mock private OrcamentoRepositoryPort orcamentoRepository;
    @Mock private ExecucaoRepositoryPort execucaoRepository;
    @Mock private PagamentoRepositoryPort pagamentoRepository;
    @Mock private EntregaRepositoryPort entregaRepository;
    @Mock private EncerramentoRepositoryPort encerramentoRepository;
    @Mock private PagamentoGatewayPort pagamentoGateway;
    @Mock private NotificarStatusOrdemServicoInputPort notificarStatusOrdemServicoInputPort;

    private OrdemDeServicoUseCase useCase;
    private Cliente cliente;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        useCase = new OrdemDeServicoUseCase(
                osRepository,
                clienteRepository,
                veiculoRepository,
                servicoRepository,
                pecaRepository,
                orcamentoRepository,
                execucaoRepository,
                pagamentoRepository,
                entregaRepository,
                encerramentoRepository,
                pagamentoGateway,
                notificarStatusOrdemServicoInputPort);

        cliente = Cliente.builder()
                .id(1L)
                .documento(new CpfCnpj("52998224725"))
                .nome("Maria")
                .build();

        veiculo = Veiculo.builder()
                .id(1L)
                .placa(new Placa("ABC1D23"))
                .marca("Honda")
                .modelo("Civic")
                .ano(2022)
                .clienteId(1L)
                .cliente(cliente)
                .build();
    }

    private OrdemDeServico osComItem() {
        var os = OrdemDeServico.criar(cliente, veiculo);
        os.atribuirId(1L);
        os.atribuirNumero("OS-2026-00001");
        os.adicionarItem(ItemOS.builder()
                .tipo(TipoItem.SERVICO)
                .descricao("Revisao")
                .quantidade(1)
                .valorUnitario(new BigDecimal("200.00"))
                .build());
        return os;
    }

    @Test
    @Story("Criar Ordem de Servico")
    void deveCriarOS() {
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(1L)).thenReturn(Optional.of(veiculo));
        when(osRepository.salvar(any())).thenAnswer(inv -> {
            OrdemDeServico os = inv.getArgument(0);
            if (os.getId() == null) {
                os.atribuirId(10L);
            }
            return os;
        });
        when(execucaoRepository.salvar(any())).thenReturn(Execucao.criar(10L));

        var resp = useCase.execute(new CriarOrdemServicoCommand(1L, 1L, null, "atendente"));

        assertEquals("RECEBIDA", resp.status());
        assertEquals("OS-2026-00010", resp.numero());
        verify(osRepository, times(2)).salvar(any());
        verify(execucaoRepository).salvar(any());
    }

    @Test
    @Story("Criar OS com itens")
    void deveCriarOSComItens() {
        var servico = new Servico("Troca oleo", "desc", new BigDecimal("100"), 30);
        var peca = Peca.builder()
                .id(2L)
                .nome("Filtro")
                .quantidadeEstoque(new Quantidade(10))
                .valorUnitario(new BigDecimal("50"))
                .build();

        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(1L)).thenReturn(Optional.of(veiculo));
        when(servicoRepository.buscarPorId(1L)).thenReturn(Optional.of(servico));
        when(pecaRepository.buscarPorId(2L)).thenReturn(Optional.of(peca));
        when(osRepository.salvar(any())).thenAnswer(inv -> {
            OrdemDeServico os = inv.getArgument(0);
            if (os.getId() == null) {
                os.atribuirId(11L);
            }
            return os;
        });
        when(execucaoRepository.salvar(any())).thenReturn(Execucao.criar(11L));

        var itens = List.of(new ItemOSCommand("SERVICO", 1L, 1), new ItemOSCommand("PECA", 2L, 2));
        var resp = useCase.execute(new CriarOrdemServicoCommand(1L, 1L, itens, "atendente"));

        assertEquals(new BigDecimal("200"), resp.valorTotal());
    }

    @Test
    @Story("Rejeitar criacao sem cliente")
    void naoDeveCriarOSSemCliente() {
        when(clienteRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> useCase.execute(new CriarOrdemServicoCommand(99L, 1L, null, "atendente")));
    }

    @Test
    @Story("Buscar e listar OS")
    void deveBuscarEListar() {
        var os = osComItem();
        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(osRepository.buscarPorNumero("OS-2026-00001")).thenReturn(Optional.of(os));
        when(osRepository.listarTodas()).thenReturn(List.of(os));
        when(osRepository.listarFilaOperacional()).thenReturn(List.of(os));
        when(osRepository.listarPorCliente(1L)).thenReturn(List.of(os));
        when(osRepository.listarPorStatus(StatusOS.RECEBIDA)).thenReturn(List.of(os));
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(1L)).thenReturn(Optional.of(veiculo));

        assertEquals("RECEBIDA", useCase.execute(1L).status());
        assertEquals("RECEBIDA", useCase.execute("OS-2026-00001").status());
        assertEquals(1, useCase.execute().size());
        assertEquals(1, useCase.listarFilaOperacional().size());
        assertEquals(1, useCase.executeByCliente(1L).size());
        assertEquals(1, useCase.execute(StatusOS.RECEBIDA).size());
        assertEquals("RECEBIDA", useCase.executeStatus(1L));
    }

    @Test
    @Story("Iniciar diagnostico")
    void deveIniciarDiagnostico() {
        var os = osComItem();
        var execucao = Execucao.criar(1L);
        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(execucaoRepository.buscarPorOrdemDeServico(1L)).thenReturn(Optional.of(execucao));
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(1L)).thenReturn(Optional.of(veiculo));

        var resp = useCase.execute(new IniciarDiagnosticoCommand(1L, "mecanico"));

        assertEquals("EM_DIAGNOSTICO", resp.status());
        verify(execucaoRepository).salvar(execucao);
    }

    @Test
    @Story("Gerar orcamento")
    void deveGerarOrcamento() {
        var os = osComItem();
        os.avancarParaDiagnostico();
        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(orcamentoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        var resp = useCase.execute(new GerarOrcamentoCommand(1L));

        assertEquals("ENVIADO", resp.status());
        assertEquals(new BigDecimal("200.00"), resp.valorTotal());
    }

    @Test
    @Story("Rejeitar geracao de orcamento sem itens")
    void naoDeveGerarOrcamentoSemItens() {
        var os = OrdemDeServico.criar(cliente, veiculo);
        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));

        assertThrows(NegocioException.class, () -> useCase.execute(new GerarOrcamentoCommand(1L)));
    }

    @Test
    @Story("Rejeitar geracao de orcamento em status invalido")
    void naoDeveGerarOrcamentoEmStatusInvalido() {
        var os = osComItem();
        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));

        var ex = assertThrows(IllegalStateException.class,
                () -> useCase.execute(new GerarOrcamentoCommand(1L)));

        assertTrue(ex.getMessage().contains("Nao e possivel gerar orcamento"));
        verify(orcamentoRepository, never()).salvar(any());
        verify(osRepository, never()).salvar(any());
    }

    @Test
    @Story("Aprovar orcamento com baixa de estoque")
    void deveAprovarOrcamentoComBaixaEstoque() {
        var os = OrdemDeServico.criar(cliente, veiculo);
        var itemPeca = ItemOS.builder()
                .tipo(TipoItem.PECA)
                .descricao("Filtro")
                .quantidade(2)
                .valorUnitario(new BigDecimal("50"))
                .referenciaId(10L)
                .build();
        os.adicionarItem(itemPeca);
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();

        var peca = Peca.builder()
                .id(10L)
                .nome("Filtro")
                .quantidadeEstoque(new Quantidade(10))
                .valorUnitario(new BigDecimal("50"))
                .build();
        var orcamento = Orcamento.gerar(1L, new BigDecimal("100"));
        orcamento.enviar();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(orcamentoRepository.buscarAtivoByOrdemDeServico(1L)).thenReturn(Optional.of(orcamento));
        when(pecaRepository.buscarPorId(10L)).thenReturn(Optional.of(peca));
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(1L)).thenReturn(Optional.of(veiculo));

        var resp = useCase.execute(new AprovarOrcamentoCommand(1L));

        assertEquals("EM_EXECUCAO", resp.status());
        assertEquals(8, peca.getQuantidadeEstoqueValor());
    }

    @Test
    @Story("Aprovar orcamento em status invalido retorna regra de negocio")
    void deveTraduzirTransicaoInvalidaAoAprovarOrcamentoParaNegocioException() {
        var os = osComItem();
        var orcamento = Orcamento.gerar(1L, new BigDecimal("200.00"));
        orcamento.enviar();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(orcamentoRepository.buscarAtivoByOrdemDeServico(1L)).thenReturn(Optional.of(orcamento));

        var ex = assertThrows(NegocioException.class,
                () -> useCase.execute(new AprovarOrcamentoCommand(1L)));

        assertTrue(ex.getMessage().contains("Transição inválida"));
        verify(orcamentoRepository).salvar(orcamento);
        verify(osRepository, never()).salvar(any());
    }

    @Test
    @Story("Finalizar e registrar pagamento")
    void deveFinalizarERegistrarPagamento() {
        var os = osComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();
        os.aprovarEIniciarExecucao();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(1L)).thenReturn(Optional.of(veiculo));

        assertEquals("FINALIZADA", useCase.execute(new FinalizarServicoCommand(1L)).status());

        when(pagamentoGateway.processar(any())).thenReturn(
                PagamentoGatewayPort.GatewayResponse.aprovado("TX-1", "ok"));

        var pago = useCase.execute(new RegistrarPagamentoCommand(1L, MetodoPagamento.PIX.name()));

        assertEquals("AGUARDANDO_RETIRADA", pago.status());
        verify(pagamentoRepository).salvar(any());
    }

    @Test
    @Story("Pagamento recusado")
    void deveLancarErroQuandoGatewayRecusaPagamento() {
        var os = osComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();
        os.aprovarEIniciarExecucao();
        os.finalizar();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(pagamentoGateway.processar(any())).thenReturn(
                PagamentoGatewayPort.GatewayResponse.recusado("TX-2", "saldo insuficiente"));

        var ex = assertThrows(NegocioException.class,
                () -> useCase.execute(new RegistrarPagamentoCommand(1L, MetodoPagamento.PIX.name())));
        assertTrue(ex.getMessage().contains("recusado"));
        verify(pagamentoRepository).salvar(any());
        verify(entregaRepository, never()).salvar(any());
    }

    @Test
    @Story("Metricas")
    void deveCalcularMetricasETempoMedio() {
        var os = osComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();
        os.aprovarEIniciarExecucao();
        os.finalizar();
        os.aguardarRetirada();
        os.entregar();

        var entrega = Entrega.builder()
                .ordemDeServicoId(1L)
                .dataEntrega(os.getDataCriacao().plusMinutes(120))
                .build();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(osRepository.listarPorStatus(StatusOS.ENTREGUE)).thenReturn(List.of(os));
        when(entregaRepository.buscarPorOrdemDeServico(1L)).thenReturn(Optional.of(entrega));

        var metricas = useCase.executeMetricas(1L);
        var tempo = useCase.executeTempoMedio();

        assertNotNull(metricas.tempoExecucao());
        assertEquals("2h", metricas.tempoAtendimento());
        assertTrue(tempo.tempoMedioAtendimento() > 0);
    }

    @Test
    @Story("Tempo medio vazio")
    void deveRetornarZeroParaTempoMedioVazio() {
        when(osRepository.listarPorStatus(StatusOS.ENTREGUE)).thenReturn(Collections.emptyList());

        var tempo = useCase.executeTempoMedio();

        assertEquals(0.0, tempo.tempoMedioExecucao());
        assertEquals(0.0, tempo.tempoMedioAtendimento());
    }

    @Test
    @Story("Metricas sem entrega")
    void deveCalcularMetricasSemEntrega() {
        var os = osComItem();
        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(entregaRepository.buscarPorOrdemDeServico(1L)).thenReturn(Optional.empty());

        var metricas = useCase.executeMetricas(1L);

        assertNull(metricas.dataEntrega());
        assertNull(metricas.tempoAtendimento());
    }

    @Test
    @Story("Formatar duracao")
    void deveFormatarDuracaoComHorasEMinutos() {
        var os = OrdemDeServico.builder()
                .id(1L)
                .numero("OS-2026-00001")
                .status(StatusOS.FINALIZADA)
                .dataCriacao(LocalDateTime.of(2026, 1, 1, 8, 0))
                .dataFinalizacao(LocalDateTime.of(2026, 1, 1, 9, 30))
                .clienteId(1L)
                .cliente(cliente)
                .veiculoId(1L)
                .veiculo(veiculo)
                .build();
        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(entregaRepository.buscarPorOrdemDeServico(1L)).thenReturn(Optional.empty());

        var metricas = useCase.executeMetricas(1L);

        assertEquals("1h 30min", metricas.tempoExecucao());
    }
}
