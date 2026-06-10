package br.com.oficina.application.service;

import br.com.oficina.application.dto.CriarOrdemServicoRequest;
import br.com.oficina.application.dto.ItemOSRequest;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.domain.atendimento.cliente.Cliente;
import br.com.oficina.domain.atendimento.cliente.ClienteRepository;
import br.com.oficina.domain.atendimento.cliente.vo.CpfCnpj;
import br.com.oficina.domain.atendimento.veiculo.Veiculo;
import br.com.oficina.domain.atendimento.veiculo.VeiculoRepository;
import br.com.oficina.domain.atendimento.veiculo.vo.Placa;
import br.com.oficina.domain.encerramento.EncerramentoRepository;
import br.com.oficina.domain.entrega.EntregaRepository;
import br.com.oficina.domain.estoque.Peca;
import br.com.oficina.domain.estoque.PecaRepository;
import br.com.oficina.domain.estoque.vo.Quantidade;
import br.com.oficina.domain.execucao.Execucao;
import br.com.oficina.domain.execucao.ExecucaoRepository;
import br.com.oficina.domain.execucao.StatusExecucao;
import br.com.oficina.domain.financeiro.PagamentoGateway;
import br.com.oficina.domain.financeiro.PagamentoRepository;
import br.com.oficina.domain.orcamento.Orcamento;
import br.com.oficina.domain.orcamento.OrcamentoRepository;
import br.com.oficina.domain.orcamento.StatusOrcamento;
import java.time.LocalDateTime;
import br.com.oficina.domain.ordemservico.*;
import br.com.oficina.domain.servico.Servico;
import br.com.oficina.domain.servico.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

@ExtendWith(MockitoExtension.class)
@Epic("Ordem de Serviço")
@Feature("Serviço de Ordens de Serviço")
class OrdemDeServicoServiceTest {

    @Mock private OrdemDeServicoRepository osRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private VeiculoRepository veiculoRepository;
    @Mock private ServicoRepository servicoRepository;
    @Mock private PecaRepository pecaRepository;
    @Mock private OrcamentoRepository orcamentoRepository;
    @Mock private ExecucaoRepository execucaoRepository;
    @Mock private PagamentoRepository pagamentoRepository;
    @Mock private EntregaRepository entregaRepository;
    @Mock private EncerramentoRepository encerramentoRepository;
    @Mock private PagamentoGateway pagamentoGateway;

    @InjectMocks
    private OrdemDeServicoService service;

    private Cliente cliente;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
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
                .cliente(cliente)
                .build();
    }

    private OrdemDeServico criarOSComItem() {
        var os = OrdemDeServico.criar(cliente, veiculo);
        os.adicionarItem(ItemOS.builder()
                .tipo(TipoItem.SERVICO)
                .descricao("Revisão")
                .quantidade(1)
                .valorUnitario(new BigDecimal("200.00"))
                .build());
        return os;
    }

    // -----------------------------------------------------------------------
    // criarOS
    // -----------------------------------------------------------------------

    @Test
    @Story("Criar Ordem de Serviço")
    void deveCriarOS() {
        var req = new CriarOrdemServicoRequest(1L, 1L, null);

        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(1L)).thenReturn(Optional.of(veiculo));
        when(osRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(execucaoRepository.salvar(any())).thenReturn(Execucao.criar(1L));

        var resp = service.criarOS(req);

        assertNotNull(resp);
        assertEquals("RECEBIDA", resp.status());
        verify(osRepository, times(2)).salvar(any());
        verify(execucaoRepository).salvar(any());
    }

    @Test
    @Story("Criar OS com itens de serviço")
    void deveCriarOSComItensServico() {
        var servico = new Servico("Troca oleo", "desc", new BigDecimal("100"), 30);
        var itens = List.of(new ItemOSRequest("SERVICO", 1L, 1));
        var req = new CriarOrdemServicoRequest(1L, 1L, itens);

        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(1L)).thenReturn(Optional.of(veiculo));
        when(servicoRepository.buscarPorId(1L)).thenReturn(Optional.of(servico));
        when(osRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(execucaoRepository.salvar(any())).thenReturn(Execucao.criar(1L));

        var resp = service.criarOS(req);
        assertNotNull(resp);
    }

    @Test
    @Story("Criar OS com itens de peça")
    void deveCriarOSComItensPeca() {
        var peca = Peca.builder().id(1L).nome("Filtro").quantidadeEstoque(new Quantidade(10))
                .valorUnitario(new BigDecimal("50")).build();
        var itens = List.of(new ItemOSRequest("PECA", 1L, 2));
        var req = new CriarOrdemServicoRequest(1L, 1L, itens);

        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(1L)).thenReturn(Optional.of(veiculo));
        when(pecaRepository.buscarPorId(1L)).thenReturn(Optional.of(peca));
        when(osRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(execucaoRepository.salvar(any())).thenReturn(Execucao.criar(1L));

        var resp = service.criarOS(req);
        assertNotNull(resp);
    }

    @Test
    @Story("Criar OS com lista de itens vazia")
    void deveCriarOSComItensVazios() {
        var req = new CriarOrdemServicoRequest(1L, 1L, List.of());

        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(1L)).thenReturn(Optional.of(veiculo));
        when(osRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(execucaoRepository.salvar(any())).thenReturn(Execucao.criar(1L));

        var resp = service.criarOS(req);
        assertEquals("RECEBIDA", resp.status());
    }

    @Test
    @Story("Rejeitar criação de OS sem cliente")
    void naoDeveCriarOSSemCliente() {
        var req = new CriarOrdemServicoRequest(99L, 1L, null);
        when(clienteRepository.buscarPorId(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.criarOS(req));
    }

    @Test
    @Story("Rejeitar criação de OS sem veículo")
    void naoDeveCriarOSSemVeiculo() {
        var req = new CriarOrdemServicoRequest(1L, 99L, null);
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.criarOS(req));
    }

    @Test
    @Story("Rejeitar OS com serviço inexistente")
    void naoDeveCriarOSComServicoInexistente() {
        var itens = List.of(new ItemOSRequest("SERVICO", 99L, 1));
        var req = new CriarOrdemServicoRequest(1L, 1L, itens);

        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(1L)).thenReturn(Optional.of(veiculo));
        when(servicoRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.criarOS(req));
    }

    @Test
    @Story("Rejeitar OS com peça inexistente")
    void naoDeveCriarOSComPecaInexistente() {
        var itens = List.of(new ItemOSRequest("PECA", 99L, 1));
        var req = new CriarOrdemServicoRequest(1L, 1L, itens);

        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(1L)).thenReturn(Optional.of(veiculo));
        when(pecaRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.criarOS(req));
    }

    // -----------------------------------------------------------------------
    // buscarPorId / buscarPorNumero / listar*
    // -----------------------------------------------------------------------

    @Test
    @Story("Buscar OS por ID")
    void deveBuscarPorId() {
        var os = OrdemDeServico.criar(cliente, veiculo);
        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        var resp = service.buscarPorId(1L);
        assertNotNull(resp);
    }

    @Test
    @Story("Não encontrar OS inexistente")
    void naoDeveEncontrarOsInexistente() {
        when(osRepository.buscarPorId(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    @Test
    @Story("Buscar OS por número")
    void deveBuscarPorNumero() {
        var os = criarOSComItem();
        when(osRepository.buscarPorNumero("OS-2026-00001")).thenReturn(Optional.of(os));

        var resp = service.buscarPorNumero("OS-2026-00001");

        assertEquals("RECEBIDA", resp.status());
    }

    @Test
    @Story("Lançar erro quando OS não encontrada por número")
    void deveLancarErroQuandoOSNaoEncontradaPorNumero() {
        when(osRepository.buscarPorNumero("OS-2026-99999")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.buscarPorNumero("OS-2026-99999"));
    }

    @Test
    @Story("Listar todas as Ordens de Serviço")
    void deveListarTodas() {
        var os = OrdemDeServico.criar(cliente, veiculo);
        when(osRepository.listarTodas()).thenReturn(List.of(os));
        assertEquals(1, service.listarTodas().size());
    }

    @Test
    @Story("Listar OS por cliente")
    void deveListarPorCliente() {
        when(osRepository.listarPorCliente(1L)).thenReturn(List.of());
        assertEquals(0, service.listarPorCliente(1L).size());
    }

    @Test
    @Story("Listar OS por status")
    void deveListarPorStatus() {
        var os = criarOSComItem();
        when(osRepository.listarPorStatus(StatusOS.RECEBIDA)).thenReturn(List.of(os));

        var lista = service.listarPorStatus(StatusOS.RECEBIDA);

        assertEquals(1, lista.size());
    }

    @Test
    @Story("Consultar status da OS")
    void deveConsultarStatus() {
        var os = criarOSComItem();
        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));

        assertEquals("RECEBIDA", service.consultarStatus(1L));
    }

    // -----------------------------------------------------------------------
    // iniciarDiagnostico
    // -----------------------------------------------------------------------

    @Test
    @Story("Iniciar diagnóstico da OS")
    void deveIniciarDiagnostico() {
        var os = OrdemDeServico.criar(cliente, veiculo);
        var execucao = Execucao.criar(1L);
        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(osRepository.salvar(any())).thenReturn(os);
        when(execucaoRepository.buscarPorOrdemDeServico(1L)).thenReturn(Optional.of(execucao));
        when(execucaoRepository.salvar(any())).thenReturn(execucao);

        var resp = service.iniciarDiagnostico(1L);

        assertEquals("EM_DIAGNOSTICO", resp.status());
        assertEquals(StatusExecucao.EM_DIAGNOSTICO, execucao.getStatus());
    }

    @Test
    @Story("Rejeitar início de diagnóstico em OS não RECEBIDA")
    void naoDeveIniciarDiagnosticoSeNaoEstiverRecebida() {
        var os = criarOSComItem();
        os.avancarParaDiagnostico(); // já está em EM_DIAGNOSTICO
        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        assertThrows(IllegalStateException.class, () -> service.iniciarDiagnostico(1L));
    }

    // -----------------------------------------------------------------------
    // gerarOrcamento
    // -----------------------------------------------------------------------

    @Test
    @Story("Gerar orçamento para OS")
    void deveGerarOrcamento() {
        var os = criarOSComItem();
        os.avancarParaDiagnostico();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(orcamentoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(osRepository.salvar(any())).thenReturn(os);

        var resp = service.gerarOrcamento(1L);

        assertEquals("ENVIADO", resp.status());
        assertEquals(new BigDecimal("200.00"), resp.valorTotal());
    }

    @Test
    @Story("Gerar orçamento com OS em execução")
    void deveGerarOrcamentoComOSEmExecucao() {
        // Novo problema identificado durante execução — EM_EXECUCAO também é válido
        var os = criarOSComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();
        os.aprovarEIniciarExecucao();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(orcamentoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(osRepository.salvar(any())).thenReturn(os);

        var resp = service.gerarOrcamento(1L);
        assertNotNull(resp);
        assertEquals("ENVIADO", resp.status());
    }

    @Test
    @Story("Rejeitar geração de orçamento com OS RECEBIDA")
    void naoDeveGerarOrcamentoComOSRecebida() {
        var os = criarOSComItem(); // status RECEBIDA — diagnóstico não iniciado
        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        assertThrows(IllegalStateException.class, () -> service.gerarOrcamento(1L));
    }

    @Test
    @Story("Rejeitar geração de orçamento sem itens")
    void naoDeveGerarOrcamentoSemItens() {
        var os = OrdemDeServico.criar(cliente, veiculo);
        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        assertThrows(NegocioException.class, () -> service.gerarOrcamento(1L));
    }

    // -----------------------------------------------------------------------
    // aprovarOrcamento
    // -----------------------------------------------------------------------

    @Test
    @Story("Aprovar orçamento")
    void deveAprovarOrcamento() {
        // FIX: aprovarOrcamento não toca execucaoRepository — stubs e assert de status
        //      de execução removidos (confirmado por deveAprovarComItemServicoSemBaixaEstoque
        //      que passa sem qualquer stub de execucaoRepository).
        var os = criarOSComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();

        var orc = Orcamento.gerar(1L, new BigDecimal("200"));
        orc.enviar();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(orcamentoRepository.buscarAtivoByOrdemDeServico(1L)).thenReturn(Optional.of(orc));
        when(osRepository.salvar(any())).thenReturn(os);

        var resp = service.aprovarOrcamento(1L);

        assertEquals("EM_EXECUCAO", resp.status());
    }

    @Test
    @Story("Rejeitar aprovação de orçamento expirado")
    void naoDeveAprovarOrcamentoExpirado() {
        var os = criarOSComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();

        var orc = Orcamento.builder()
                .ordemDeServicoId(1L)
                .valorTotal(new BigDecimal("200"))
                .status(StatusOrcamento.ENVIADO)
                .dataCriacao(LocalDateTime.now().minusDays(30))
                .dataValidade(LocalDateTime.now().minusDays(1))
                .build();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(orcamentoRepository.buscarAtivoByOrdemDeServico(1L)).thenReturn(Optional.of(orc));

        assertThrows(IllegalStateException.class, () -> service.aprovarOrcamento(1L));
    }

    @Test
    @Story("Rejeitar aprovação sem orçamento ativo")
    void naoDeveAprovarOrcamentoSemOrcamentoAtivo() {
        var os = criarOSComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(orcamentoRepository.buscarAtivoByOrdemDeServico(1L)).thenReturn(Optional.empty());

        assertThrows(NegocioException.class, () -> service.aprovarOrcamento(1L));
    }

    @Test
    @Story("Rejeitar aprovação com peça inexistente")
    void naoDeveAprovarOrcamentoComPecaInexistente() {
        var os = OrdemDeServico.criar(cliente, veiculo);
        os.adicionarItem(ItemOS.builder()
                .tipo(TipoItem.PECA)
                .descricao("Filtro")
                .quantidade(2)
                .valorUnitario(new BigDecimal("50"))
                .referenciaId(10L)
                .build());
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();

        var orc = Orcamento.gerar(1L, new BigDecimal("100"));
        orc.enviar();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(orcamentoRepository.buscarAtivoByOrdemDeServico(1L)).thenReturn(Optional.of(orc));
        when(pecaRepository.buscarPorId(10L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.aprovarOrcamento(1L));
    }

    @Test
    @Story("Aprovar orçamento com baixa de estoque")
    void deveAprovarOrcamentoComBaixaEstoque() {
        var os = OrdemDeServico.criar(cliente, veiculo);
        var itemPeca = ItemOS.builder()
                .tipo(TipoItem.PECA).descricao("Filtro").quantidade(2)
                .valorUnitario(new BigDecimal("50")).referenciaId(10L).build();
        os.adicionarItem(itemPeca);
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();

        var peca = Peca.builder().id(10L).nome("Filtro").quantidadeEstoque(new Quantidade(10))
                .valorUnitario(new BigDecimal("50")).build();
        var orc = Orcamento.gerar(1L, new BigDecimal("100"));
        orc.enviar();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(orcamentoRepository.buscarAtivoByOrdemDeServico(1L)).thenReturn(Optional.of(orc));
        when(osRepository.salvar(any())).thenReturn(os);
        when(pecaRepository.buscarPorId(10L)).thenReturn(Optional.of(peca));
        when(pecaRepository.salvar(any())).thenReturn(peca);

        service.aprovarOrcamento(1L);
        assertEquals(8, peca.getQuantidadeEstoqueValor());
    }

    @Test
    @Story("Aprovar com item de serviço sem baixa de estoque")
    void deveAprovarComItemServicoSemBaixaEstoque() {
        var os = criarOSComItem(); // item tipo SERVICO
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();

        var orc = Orcamento.gerar(1L, new BigDecimal("200"));
        orc.enviar();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(orcamentoRepository.buscarAtivoByOrdemDeServico(1L)).thenReturn(Optional.of(orc));
        when(osRepository.salvar(any())).thenReturn(os);

        service.aprovarOrcamento(1L);
        verify(pecaRepository, never()).buscarPorId(anyLong());
    }

    @Test
    @Story("Rejeitar baixa duplicada de estoque")
    void naoDeveReduplicarBaixaEstoque() {
        var os = OrdemDeServico.criar(cliente, veiculo);
        var itemPecaJaReduzida = ItemOS.builder()
                .tipo(TipoItem.PECA).descricao("Filtro").quantidade(1)
                .valorUnitario(new BigDecimal("50")).referenciaId(10L)
                .estoqueReduzido(true)
                .build();
        os.adicionarItem(itemPecaJaReduzida);
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();

        var orc = Orcamento.gerar(1L, new BigDecimal("50"));
        orc.enviar();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(orcamentoRepository.buscarAtivoByOrdemDeServico(1L)).thenReturn(Optional.of(orc));
        when(osRepository.salvar(any())).thenReturn(os);

        service.aprovarOrcamento(1L);

        verify(pecaRepository, never()).buscarPorId(anyLong());
        verify(pecaRepository, never()).salvar(any());
    }

    @Test
    @Story("Aprovar sem duplicar baixa de estoque em novo problema")
    void deveAprovarSemDuplicarBaixaEstoqueNovoProblema() {
        var os = OrdemDeServico.criar(cliente, veiculo);
        var itemJaBaixado = ItemOS.builder()
                .tipo(TipoItem.PECA).descricao("Filtro").quantidade(2)
                .valorUnitario(new BigDecimal("50")).referenciaId(10L).build();
        itemJaBaixado.marcarEstoqueReduzido();

        var itemNovo = ItemOS.builder()
                .tipo(TipoItem.PECA).descricao("Vela").quantidade(4)
                .valorUnitario(new BigDecimal("30")).referenciaId(20L).build();
        os.adicionarItem(itemJaBaixado);
        os.adicionarItem(itemNovo);
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();

        var orc = Orcamento.gerar(1L, new BigDecimal("220"));
        orc.enviar();
        var peca = Peca.builder().id(20L).nome("Vela")
                .quantidadeEstoque(new Quantidade(10)).valorUnitario(new BigDecimal("30")).build();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(orcamentoRepository.buscarAtivoByOrdemDeServico(1L)).thenReturn(Optional.of(orc));
        when(osRepository.salvar(any())).thenReturn(os);
        when(pecaRepository.buscarPorId(20L)).thenReturn(Optional.of(peca));
        when(pecaRepository.salvar(any())).thenReturn(peca);

        service.aprovarOrcamento(1L);

        verify(pecaRepository, never()).buscarPorId(10L);
        verify(pecaRepository).buscarPorId(20L);
        assertEquals(6, peca.getQuantidadeEstoqueValor());
    }

    // -----------------------------------------------------------------------
    // rejeitarOrcamento
    // -----------------------------------------------------------------------

    @Test
    @Story("Rejeitar orçamento")
    void deveRejeitarOrcamento() {
        var os = criarOSComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();

        var orc = Orcamento.gerar(1L, new BigDecimal("200"));
        orc.enviar();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(orcamentoRepository.buscarAtivoByOrdemDeServico(1L)).thenReturn(Optional.of(orc));
        when(osRepository.salvar(any())).thenReturn(os);

        var resp = service.rejeitarOrcamento(1L);
        assertEquals("CANCELADA", resp.status());
    }

    @Test
    void rejeitarSemOrcamento_lanca() {
        var os = criarOSComItem();
        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(orcamentoRepository.buscarAtivoByOrdemDeServico(1L)).thenReturn(Optional.empty());
        assertThrows(NegocioException.class, () -> service.rejeitarOrcamento(1L));
    }

    // -----------------------------------------------------------------------
    // finalizarServico
    // -----------------------------------------------------------------------

    @Test
    @Story("Finalizar serviço da OS")
    void deveFinalizarServico() {
        var os = criarOSComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();
        os.aprovarEIniciarExecucao();

        var exec = Execucao.criar(1L);
        exec.iniciarServico(); // EM_ANDAMENTO

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(osRepository.salvar(any())).thenReturn(os);
        when(execucaoRepository.buscarPorOrdemDeServico(1L)).thenReturn(Optional.of(exec));
        when(execucaoRepository.salvar(any())).thenReturn(exec);

        var resp = service.finalizarServico(1L);
        assertEquals("FINALIZADA", resp.status());
    }

    @Test
    @Story("Finalizar serviço com execução aguardando")
    void deveFinalizarServicoComExecucaoAguardando() {
        // Execucao em AGUARDANDO — iniciarServico() de AGUARDANDO é permitido pelo domínio,
        // portanto o service avança AGUARDANDO → EM_ANDAMENTO → SERVICO_FINALIZADO internamente.
        var os = criarOSComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();
        os.aprovarEIniciarExecucao();

        var exec = Execucao.criar(1L); // status inicial = AGUARDANDO

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(osRepository.salvar(any())).thenReturn(os);
        when(execucaoRepository.buscarPorOrdemDeServico(1L)).thenReturn(Optional.of(exec));
        when(execucaoRepository.salvar(any())).thenReturn(exec);

        var resp = service.finalizarServico(1L);

        assertEquals("FINALIZADA", resp.status());
        assertEquals(StatusExecucao.SERVICO_FINALIZADO, exec.getStatus());
    }

    @Test
    @Story("Finalizar serviço sem execução")
    void deveFinalizarServicoSemExecucao() {
        var os = criarOSComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();
        os.aprovarEIniciarExecucao();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(osRepository.salvar(any())).thenReturn(os);
        when(execucaoRepository.buscarPorOrdemDeServico(1L)).thenReturn(Optional.empty());

        var resp = service.finalizarServico(1L);
        assertEquals("FINALIZADA", resp.status());
        verify(execucaoRepository, never()).salvar(any());
    }

    // -----------------------------------------------------------------------
    // registrarPagamento
    // -----------------------------------------------------------------------

    @Test
    @Story("Registrar pagamento da OS")
    void deveRegistrarPagamento() {
        var os = criarOSComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();
        os.aprovarEIniciarExecucao();
        os.finalizar();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(osRepository.salvar(any())).thenReturn(os);
        when(pagamentoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(pagamentoGateway.processar(any())).thenReturn(
                PagamentoGateway.GatewayResponse.aprovado("TX-1", "ok"));

        var resp = service.registrarPagamento(1L, "PIX");
        assertEquals("AGUARDANDO_RETIRADA", resp.status());
        verify(pagamentoGateway).processar(any());
        verify(entregaRepository, never()).salvar(any());
        verify(encerramentoRepository, never()).salvar(any());
    }

    @Test
    @Story("Lançar erro quando gateway recusa pagamento")
    void deveLancarErroQuandoGatewayRecusaPagamento() {
        var os = criarOSComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();
        os.aprovarEIniciarExecucao();
        os.finalizar();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(pagamentoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(pagamentoGateway.processar(any())).thenReturn(
                PagamentoGateway.GatewayResponse.recusado("TX-2", "saldo insuficiente"));

        var ex = assertThrows(NegocioException.class, () -> service.registrarPagamento(1L, "PIX"));
        assertTrue(ex.getMessage().contains("recusado"));
        verify(pagamentoRepository).salvar(any());
        verify(entregaRepository, never()).salvar(any());
        verify(encerramentoRepository, never()).salvar(any());
    }

    @Test
    @Story("Rejeitar pagamento de OS não finalizada")
    void naoDevePagarOSNaoFinalizada() {
        var os = criarOSComItem();
        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        assertThrows(NegocioException.class, () -> service.registrarPagamento(1L, "PIX"));
    }

    // -----------------------------------------------------------------------
    // entregarVeiculo
    // -----------------------------------------------------------------------

    @Test
    @Story("Entregar veículo ao cliente")
    void deveEntregarVeiculo() {
        var os = criarOSComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();
        os.aprovarEIniciarExecucao();
        os.finalizar();
        os.aguardarRetirada();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(osRepository.salvar(any())).thenReturn(os);
        when(entregaRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(encerramentoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        var resp = service.entregarVeiculo(1L);
        assertEquals("ENTREGUE", resp.status());
        verify(entregaRepository).salvar(any());
        verify(encerramentoRepository).salvar(any());
    }

    @Test
    @Story("Rejeitar entrega sem pagamento")
    void naoDeveEntregarVeiculoSemPagamento() {
        var os = criarOSComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();
        os.aprovarEIniciarExecucao();
        os.finalizar();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        assertThrows(IllegalStateException.class, () -> service.entregarVeiculo(1L));
    }

    // -----------------------------------------------------------------------
    // adicionarItens
    // -----------------------------------------------------------------------

    @Test
    @Story("Adicionar itens a OS em execução")
    void deveAdicionarItensAOSEmExecucao() {
        var os = criarOSComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();
        os.aprovarEIniciarExecucao();

        var servico = new br.com.oficina.domain.servico.Servico(
                "Ajuste freios", "desc", new BigDecimal("150"), 30);
        var req = List.of(new ItemOSRequest("SERVICO", 1L, 1));

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(servicoRepository.buscarPorId(1L)).thenReturn(Optional.of(servico));
        when(osRepository.salvar(any())).thenReturn(os);

        var resp = service.adicionarItens(1L, req);

        assertEquals("EM_EXECUCAO", resp.status());
        assertEquals(2, os.getItens().size());
    }

    @Test
    @Story("Rejeitar adição de itens a OS em status inválido")
    void naoDeveAdicionarItensAOSEmStatusInvalido() {
        var os = criarOSComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();
        os.aprovarEIniciarExecucao();
        os.finalizar(); // FINALIZADA — não permite novos itens

        var req = List.of(new ItemOSRequest("SERVICO", 1L, 1));
        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));

        assertThrows(NegocioException.class, () -> service.adicionarItens(1L, req));
    }

    // -----------------------------------------------------------------------
    // calcularTempoMedioExecucao
    // -----------------------------------------------------------------------

    @Test
    @Story("Calcular tempo médio de execução (sem OS)")
    void deveCalcularTempoMedioExecucaoVazio() {
        when(osRepository.listarPorStatus(StatusOS.ENTREGUE)).thenReturn(Collections.emptyList());
        assertEquals(0, service.calcularTempoMedioExecucao());
    }

    @Test
    @Story("Calcular tempo médio com OS finalizadas")
    void deveCalcularTempoMedioComOSFinalizadas() {
        var os = criarOSComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();
        os.aprovarEIniciarExecucao();
        os.finalizar();
        os.aguardarRetirada();
        os.entregar();

        when(osRepository.listarPorStatus(StatusOS.ENTREGUE)).thenReturn(List.of(os));
        double tempo = service.calcularTempoMedioExecucao();
        assertTrue(tempo >= 0);
    }

    @Test
    @Story("Calcular tempo médio com OS sem data de finalização")
    void deveCalcularTempoMedioComOSSemDataFinalizacao() {
        // OS listada como ENTREGUE mas sem dataFinalizacao (edge case: dados legados sem data)
        var os = criarOSComItem();
        // não chama os.entregar() → dataFinalizacao permanece null

        when(osRepository.listarPorStatus(StatusOS.ENTREGUE)).thenReturn(List.of(os));
        double tempo = service.calcularTempoMedioExecucao();
        assertEquals(0.0, tempo); // count==0 pois dataFinalizacao é null
    }

    // -----------------------------------------------------------------------
    // calcularTempoMedioAtendimento
    // -----------------------------------------------------------------------

    @Test
    @Story("Calcular tempo médio de atendimento sem OS entregues")
    void deveRetornarZeroParaTempoMedioAtendimentoSemOS() {
        when(osRepository.listarPorStatus(StatusOS.ENTREGUE)).thenReturn(Collections.emptyList());
        assertEquals(0.0, service.calcularTempoMedioAtendimento());
    }

    @Test
    @Story("Calcular tempo médio de atendimento sem registro de entrega")
    void deveRetornarZeroParaTempoMedioAtendimentoSemEntrega() {
        var os = criarOSComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();
        os.aprovarEIniciarExecucao();
        os.finalizar();
        os.aguardarRetirada();
        os.entregar();

        when(osRepository.listarPorStatus(StatusOS.ENTREGUE)).thenReturn(List.of(os));
        when(entregaRepository.buscarPorOrdemDeServico(any())).thenReturn(Optional.empty());

        assertEquals(0.0, service.calcularTempoMedioAtendimento());
    }

    @Test
    @Story("Calcular tempo médio de atendimento com entrega registrada")
    void deveCalcularTempoMedioAtendimentoComEntrega() {
        var os = criarOSComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();
        os.aprovarEIniciarExecucao();
        os.finalizar();
        os.aguardarRetirada();
        os.entregar();

        var entrega = mock(br.com.oficina.domain.entrega.Entrega.class);
        when(entrega.getDataEntrega())
                .thenReturn(os.getDataCriacao().plusMinutes(120));

        when(osRepository.listarPorStatus(StatusOS.ENTREGUE)).thenReturn(List.of(os));
        when(entregaRepository.buscarPorOrdemDeServico(any()))
                .thenReturn(Optional.of(entrega));

        double tempo = service.calcularTempoMedioAtendimento();
        assertTrue(tempo > 0, "Tempo médio deve ser maior que zero");
    }

    @Test
    @Story("Calcular tempo médio de atendimento com OS sem dataCriacao")
    void deveIgnorarOSSemDataCriacaoNoAtendimento() {
        var osMock = mock(br.com.oficina.domain.ordemservico.OrdemDeServico.class);
        when(osMock.getDataCriacao()).thenReturn(null);

        when(osRepository.listarPorStatus(StatusOS.ENTREGUE)).thenReturn(List.of(osMock));

        assertEquals(0.0, service.calcularTempoMedioAtendimento());
    }

    // -----------------------------------------------------------------------
    // calcularMetricasOS
    // -----------------------------------------------------------------------

    @Test
    @Story("Calcular métricas de OS finalizada com entrega")
    void deveCalcularMetricasOSComEntrega() {
        var os = criarOSComItem();
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();
        os.aprovarEIniciarExecucao();
        os.finalizar();
        os.aguardarRetirada();
        os.entregar();

        var entrega = mock(br.com.oficina.domain.entrega.Entrega.class);
        when(entrega.getDataEntrega())
                .thenReturn(os.getDataCriacao().plusMinutes(90));

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(entregaRepository.buscarPorOrdemDeServico(1L)).thenReturn(Optional.of(entrega));

        var metricas = service.calcularMetricasOS(1L);

        assertNotNull(metricas.get("dataEntrega"));
        assertEquals("ENTREGUE", metricas.get("status"));
        assertNotNull(metricas.get("tempoAtendimento")); // calculado com base na dataEntrega
    }

    @Test
    @Story("Calcular métricas de OS sem entrega registrada")
    void deveCalcularMetricasOSSemEntrega() {
        var os = criarOSComItem();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(entregaRepository.buscarPorOrdemDeServico(1L)).thenReturn(Optional.empty());

        var metricas = service.calcularMetricasOS(1L);

        assertNull(metricas.get("tempoAtendimento"));
        assertNull(metricas.get("dataEntrega"));
    }

    @Test
    @Story("Calcular métricas de OS sem dataFinalizacao")
    void deveCalcularMetricasOSSemFinalizacao() {
        var os = criarOSComItem();

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(entregaRepository.buscarPorOrdemDeServico(1L)).thenReturn(Optional.empty());

        var metricas = service.calcularMetricasOS(1L);

        assertNull(metricas.get("tempoExecucao"));
    }

    @Test
    @Story("Calcular métricas lança exceção para OS inexistente")
    void develancarExcecaoMetricasOSInexistente() {
        when(osRepository.buscarPorId(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.calcularMetricasOS(99L));
    }

    // -----------------------------------------------------------------------
    // formatarDuracao — via calcularMetricasOS com datas controladas
    // FIX: removidos stubs desnecessários (getItens, calcularValorTotal, getCliente, getVeiculo)
    //      que não são chamados por calcularMetricasOS e causavam UnnecessaryStubbingException.
    // -----------------------------------------------------------------------

    @Test
    @Story("formatarDuracao retorna '0min' para duração zero")
    void deveFormatarDuracaoZero() {
        var osMock = mock(br.com.oficina.domain.ordemservico.OrdemDeServico.class);
        var now = java.time.LocalDateTime.now();
        when(osMock.getNumero()).thenReturn("OS-2026-00001");
        when(osMock.getStatus()).thenReturn(StatusOS.FINALIZADA);
        when(osMock.getDataCriacao()).thenReturn(now);
        when(osMock.getDataFinalizacao()).thenReturn(now); // 0 min → "0min"

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(osMock));
        when(entregaRepository.buscarPorOrdemDeServico(1L)).thenReturn(Optional.empty());

        var metricas = service.calcularMetricasOS(1L);
        assertEquals("0min", metricas.get("tempoExecucao"));
    }

    @Test
    @Story("formatarDuracao retorna apenas minutos")
    void deveFormatarDuracaoApenasMinutos() {
        var osMock = mock(br.com.oficina.domain.ordemservico.OrdemDeServico.class);
        var base = java.time.LocalDateTime.of(2026, 1, 1, 8, 0);
        when(osMock.getNumero()).thenReturn("OS-2026-00001");
        when(osMock.getStatus()).thenReturn(StatusOS.FINALIZADA);
        when(osMock.getDataCriacao()).thenReturn(base);
        when(osMock.getDataFinalizacao()).thenReturn(base.plusMinutes(45)); // "45min"

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(osMock));
        when(entregaRepository.buscarPorOrdemDeServico(1L)).thenReturn(Optional.empty());

        var metricas = service.calcularMetricasOS(1L);
        assertEquals("45min", metricas.get("tempoExecucao"));
    }

    @Test
    @Story("formatarDuracao retorna horas e minutos")
    void deveFormatarDuracaoHorasEMinutos() {
        var osMock = mock(br.com.oficina.domain.ordemservico.OrdemDeServico.class);
        var base = java.time.LocalDateTime.of(2026, 1, 1, 8, 0);
        when(osMock.getNumero()).thenReturn("OS-2026-00001");
        when(osMock.getStatus()).thenReturn(StatusOS.FINALIZADA);
        when(osMock.getDataCriacao()).thenReturn(base);
        when(osMock.getDataFinalizacao()).thenReturn(base.plusMinutes(90)); // "1h 30min"

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(osMock));
        when(entregaRepository.buscarPorOrdemDeServico(1L)).thenReturn(Optional.empty());

        var metricas = service.calcularMetricasOS(1L);
        assertEquals("1h 30min", metricas.get("tempoExecucao"));
    }

    @Test
    @Story("formatarDuracao retorna dias, horas e minutos")
    void deveFormatarDuracaoDiasHorasMinutos() {
        var osMock = mock(br.com.oficina.domain.ordemservico.OrdemDeServico.class);
        var base = java.time.LocalDateTime.of(2026, 1, 1, 8, 0);
        when(osMock.getNumero()).thenReturn("OS-2026-00001");
        when(osMock.getStatus()).thenReturn(StatusOS.FINALIZADA);
        when(osMock.getDataCriacao()).thenReturn(base);
        when(osMock.getDataFinalizacao()).thenReturn(base.plusMinutes(1535)); // "1d 1h 35min"

        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(osMock));
        when(entregaRepository.buscarPorOrdemDeServico(1L)).thenReturn(Optional.empty());

        var metricas = service.calcularMetricasOS(1L);
        assertEquals("1d 1h 35min", metricas.get("tempoExecucao"));
    }
}