package br.com.oficina.application.usecase;

import br.com.oficina.application.command.AprovarOrcamentoCommand;
import br.com.oficina.application.command.DecidirOrcamentoPorTokenCommand;
import br.com.oficina.application.command.EnviarNotificacaoOrcamentoCommand;
import br.com.oficina.application.command.RejeitarOrcamentoCommand;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.AprovarOrcamentoInputPort;
import br.com.oficina.application.port.in.RejeitarOrcamentoInputPort;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.port.out.EmailNotificacaoPort;
import br.com.oficina.application.port.out.OrcamentoDecisaoClienteRepositoryPort;
import br.com.oficina.application.port.out.OrcamentoRepositoryPort;
import br.com.oficina.application.port.out.OrdemDeServicoRepositoryPort;
import br.com.oficina.application.port.out.TokenSeguroPort;
import br.com.oficina.application.query.OrdemServicoResult;
import br.com.oficina.domain.model.Cliente;
import br.com.oficina.domain.model.Orcamento;
import br.com.oficina.domain.model.OrcamentoDecisaoCliente;
import br.com.oficina.domain.model.OrdemDeServico;
import br.com.oficina.domain.model.Veiculo;
import br.com.oficina.domain.valueobject.CpfCnpj;
import br.com.oficina.domain.valueobject.Placa;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrcamentoDecisaoClienteUseCaseTest {

    @Mock private OrdemDeServicoRepositoryPort osRepository;
    @Mock private ClienteRepositoryPort clienteRepository;
    @Mock private OrcamentoRepositoryPort orcamentoRepository;
    @Mock private OrcamentoDecisaoClienteRepositoryPort decisaoRepository;
    @Mock private EmailNotificacaoPort emailNotificacaoPort;
    @Mock private TokenSeguroPort tokenSeguroPort;
    @Mock private AprovarOrcamentoInputPort aprovarOrcamentoInputPort;
    @Mock private RejeitarOrcamentoInputPort rejeitarOrcamentoInputPort;

    private OrcamentoDecisaoClienteUseCase useCase;
    private Cliente cliente;
    private OrdemDeServico os;
    private Orcamento orcamento;

    @BeforeEach
    void setUp() {
        useCase = new OrcamentoDecisaoClienteUseCase(
                osRepository,
                clienteRepository,
                orcamentoRepository,
                decisaoRepository,
                emailNotificacaoPort,
                tokenSeguroPort,
                aprovarOrcamentoInputPort,
                rejeitarOrcamentoInputPort,
                "http://localhost:8080",
                48);

        cliente = Cliente.builder()
                .id(1L)
                .documento(new CpfCnpj("52998224725"))
                .nome("Maria")
                .email("maria@email.com")
                .build();
        var veiculo = Veiculo.builder()
                .id(1L)
                .placa(new Placa("ABC1D23"))
                .marca("Honda")
                .modelo("Civic")
                .ano(2022)
                .clienteId(1L)
                .cliente(cliente)
                .build();
        os = OrdemDeServico.criar(cliente, veiculo);
        os.atribuirId(1L);
        os.atribuirNumero("OS-2026-00001");
        orcamento = Orcamento.gerar(1L, new BigDecimal("300.00"));
        orcamento = Orcamento.builder()
                .id(10L)
                .ordemDeServicoId(1L)
                .status(orcamento.getStatus())
                .valorTotal(orcamento.getValorTotal())
                .dataCriacao(orcamento.getDataCriacao())
                .dataValidade(orcamento.getDataValidade())
                .build();
        orcamento.enviar();
    }

    @Test
    void deveEnviarNotificacaoGerandoTokenHashELinks() {
        when(osRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(orcamentoRepository.buscarAtivoByOrdemDeServico(1L)).thenReturn(Optional.of(orcamento));
        when(decisaoRepository.listarPorOrcamentoEStatus(any(), any())).thenReturn(List.of());
        when(tokenSeguroPort.gerarToken()).thenReturn("token-cliente");
        when(tokenSeguroPort.gerarHash("token-cliente")).thenReturn("hash-token");
        when(decisaoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(new EnviarNotificacaoOrcamentoCommand(1L));

        assertEquals("maria@email.com", result.emailDestino());
        assertEquals("http://localhost:8080/api/orcamentos/decisoes-cliente/token-cliente/aprovar",
                result.linkAprovacao());
        verify(decisaoRepository).salvar(argThat(decisao -> "hash-token".equals(decisao.getTokenHash())));
        verify(emailNotificacaoPort).enviar(any());
    }

    @Test
    void deveAprovarPorTokenValido() {
        var decisao = OrcamentoDecisaoCliente.criar(
                10L, 1L, "hash-token", "maria@email.com", LocalDateTime.now().plusHours(1));
        when(tokenSeguroPort.gerarHash("token-cliente")).thenReturn("hash-token");
        when(decisaoRepository.buscarPorTokenHash("hash-token")).thenReturn(Optional.of(decisao));
        when(aprovarOrcamentoInputPort.execute(any(AprovarOrcamentoCommand.class)))
                .thenReturn(osResult("EM_EXECUCAO"));

        var result = useCase.execute(new DecidirOrcamentoPorTokenCommand("token-cliente"));

        assertEquals("APROVADA", result.decisao());
        verify(decisaoRepository).salvar(argThat(d -> "APROVADA".equals(d.getStatus().name())));
    }

    @Test
    void deveRecusarPorTokenValido() {
        var decisao = OrcamentoDecisaoCliente.criar(
                10L, 1L, "hash-token", "maria@email.com", LocalDateTime.now().plusHours(1));
        when(tokenSeguroPort.gerarHash("token-cliente")).thenReturn("hash-token");
        when(decisaoRepository.buscarPorTokenHash("hash-token")).thenReturn(Optional.of(decisao));
        when(rejeitarOrcamentoInputPort.execute(any(RejeitarOrcamentoCommand.class)))
                .thenReturn(osResult("CANCELADA"));

        var result = useCase.executeRecusar(new DecidirOrcamentoPorTokenCommand("token-cliente"));

        assertEquals("RECUSADA", result.decisao());
        verify(decisaoRepository).salvar(argThat(d -> "RECUSADA".equals(d.getStatus().name())));
    }

    @Test
    void deveBloquearTokenInexistente() {
        when(tokenSeguroPort.gerarHash("invalido")).thenReturn("hash-invalido");
        when(decisaoRepository.buscarPorTokenHash("hash-invalido")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> useCase.execute(new DecidirOrcamentoPorTokenCommand("invalido")));
        verify(aprovarOrcamentoInputPort, never()).execute(any());
    }

    @Test
    void deveBloquearTokenExpirado() {
        var decisao = OrcamentoDecisaoCliente.criar(
                10L, 1L, "hash-token", "maria@email.com", LocalDateTime.now().minusHours(1));
        when(tokenSeguroPort.gerarHash("token-cliente")).thenReturn("hash-token");
        when(decisaoRepository.buscarPorTokenHash("hash-token")).thenReturn(Optional.of(decisao));

        assertThrows(NegocioException.class,
                () -> useCase.execute(new DecidirOrcamentoPorTokenCommand("token-cliente")));
        verify(aprovarOrcamentoInputPort, never()).execute(any());
        verify(decisaoRepository).salvar(argThat(d -> "EXPIRADA".equals(d.getStatus().name())));
    }

    private static OrdemServicoResult osResult(String status) {
        return new OrdemServicoResult(
                1L, "OS-2026-00001", status, LocalDateTime.now(), null,
                "Maria", "529.982.247-25", "ABC1D23", "Honda Civic",
                List.of(), BigDecimal.ZERO, "atendente", null);
    }
}
