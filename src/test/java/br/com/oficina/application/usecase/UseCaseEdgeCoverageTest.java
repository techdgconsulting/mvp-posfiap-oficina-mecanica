package br.com.oficina.application.usecase;

import br.com.oficina.application.command.LoginCommand;
import br.com.oficina.application.command.AprovarOrcamentoDiretoCommand;
import br.com.oficina.application.command.AtualizarMecanicoExecucaoCommand;
import br.com.oficina.application.command.AtualizarPecaCommand;
import br.com.oficina.application.command.AtualizarServicoCommand;
import br.com.oficina.application.command.AtualizarVeiculoCommand;
import br.com.oficina.application.command.BaixarEstoqueCommand;
import br.com.oficina.application.command.EncerrarOrdemServicoCommand;
import br.com.oficina.application.command.EnviarOrcamentoCommand;
import br.com.oficina.application.command.ExcluirPecaCommand;
import br.com.oficina.application.command.ExcluirServicoCommand;
import br.com.oficina.application.command.FinalizarServicoExecucaoCommand;
import br.com.oficina.application.command.IniciarServicoExecucaoCommand;
import br.com.oficina.application.command.LiberarVeiculoEntregaCommand;
import br.com.oficina.application.command.ProcessarPagamentoCommand;
import br.com.oficina.application.command.RecusarPagamentoCommand;
import br.com.oficina.application.command.RegistrarDiagnosticoExecucaoCommand;
import br.com.oficina.application.command.RegistrarEntregaVeiculoCommand;
import br.com.oficina.application.command.ReporEstoqueCommand;
import br.com.oficina.application.command.VerificarDisponibilidadePecaCommand;
import br.com.oficina.application.exception.CredenciaisInvalidasException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.port.out.EncerramentoRepositoryPort;
import br.com.oficina.application.port.out.EntregaRepositoryPort;
import br.com.oficina.application.port.out.ExecucaoRepositoryPort;
import br.com.oficina.application.port.out.OrcamentoRepositoryPort;
import br.com.oficina.application.port.out.PagamentoGatewayPort;
import br.com.oficina.application.port.out.PagamentoRepositoryPort;
import br.com.oficina.application.port.out.PasswordHasherPort;
import br.com.oficina.application.port.out.PecaRepositoryPort;
import br.com.oficina.application.port.out.ServicoRepositoryPort;
import br.com.oficina.application.port.out.TokenProviderPort;
import br.com.oficina.application.port.out.UsuarioRepositoryPort;
import br.com.oficina.application.port.out.VeiculoRepositoryPort;
import br.com.oficina.domain.model.Encerramento;
import br.com.oficina.domain.model.Entrega;
import br.com.oficina.domain.model.Execucao;
import br.com.oficina.domain.model.Orcamento;
import br.com.oficina.domain.model.Pagamento;
import br.com.oficina.domain.model.Peca;
import br.com.oficina.domain.model.Servico;
import br.com.oficina.domain.model.Usuario;
import br.com.oficina.domain.valueobject.MetodoPagamento;
import br.com.oficina.domain.valueobject.PerfilUsuario;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UseCaseEdgeCoverageTest {

    @Test
    void autenticarLoginCobreCredenciaisInvalidasESucesso() {
        var usuarioRepository = mock(UsuarioRepositoryPort.class);
        var passwordHasher = mock(PasswordHasherPort.class);
        var tokenProvider = mock(TokenProviderPort.class);
        var useCase = new AutenticarLoginUseCase(usuarioRepository, passwordHasher, tokenProvider);
        var usuario = new Usuario(1L, "usuario", "hash", PerfilUsuario.ATENDENTE);

        when(usuarioRepository.buscarPorUsername("ausente")).thenReturn(Optional.empty());
        assertThrows(CredenciaisInvalidasException.class, () -> useCase.execute(new LoginCommand("ausente", "senha")));

        when(usuarioRepository.buscarPorUsername("usuario")).thenReturn(Optional.of(usuario));
        when(passwordHasher.matches("errada", "hash")).thenReturn(false);
        assertThrows(CredenciaisInvalidasException.class, () -> useCase.execute(new LoginCommand("usuario", "errada")));

        when(passwordHasher.matches("senha", "hash")).thenReturn(true);
        when(tokenProvider.gerarToken("usuario", "ATENDENTE")).thenReturn("token");

        var result = useCase.execute(new LoginCommand("usuario", "senha"));

        assertEquals("token", result.token());
        assertEquals("ATENDENTE", result.role());
    }

    @Test
    void registrarDiagnosticoCobreSucessoEExecucaoInexistente() {
        var execucaoRepository = mock(ExecucaoRepositoryPort.class);
        var useCase = new RegistrarDiagnosticoExecucaoUseCase(execucaoRepository);
        var execucao = Execucao.criar(1L);
        execucao.iniciarDiagnostico("Mecanico");

        when(execucaoRepository.buscarPorId(1L)).thenReturn(Optional.of(execucao));
        when(execucaoRepository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.execute(new RegistrarDiagnosticoExecucaoCommand(1L, "Falha no motor"));

        assertEquals("Falha no motor", result.descricaoProblema());
        verify(execucaoRepository).salvar(execucao);

        when(execucaoRepository.buscarPorId(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class,
                () -> useCase.execute(new RegistrarDiagnosticoExecucaoCommand(99L, "Falha")));
    }

    @Test
    void pecaUseCasesCobremAtualizacaoEstoqueDisponibilidadeEInexistentes() {
        var pecaRepository = mock(PecaRepositoryPort.class);
        var peca = Peca.builder()
                .id(10L)
                .nome("Filtro")
                .descricao("Filtro de oleo")
                .quantidadeEstoque(new br.com.oficina.domain.valueobject.Quantidade(10))
                .valorUnitario(new BigDecimal("30.00"))
                .estoqueMinimo(2)
                .build();

        when(pecaRepository.buscarPorId(10L)).thenReturn(Optional.of(peca));
        when(pecaRepository.buscarPorId(99L)).thenReturn(Optional.empty());
        when(pecaRepository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var atualizada = new AtualizarPecaUseCase(pecaRepository).execute(
                new AtualizarPecaCommand(10L, "Filtro premium", "Filtro atualizado", 12,
                        new BigDecimal("35.00"), 3));
        assertEquals("Filtro premium", atualizada.nome());
        assertEquals(12, atualizada.quantidadeEstoque());

        var aposBaixa = new BaixarEstoqueUseCase(pecaRepository).execute(new BaixarEstoqueCommand(10L, 2));
        assertEquals(10, aposBaixa.quantidadeEstoque());

        var aposReposicao = new ReporEstoqueUseCase(pecaRepository).execute(new ReporEstoqueCommand(10L, 5));
        assertEquals(15, aposReposicao.quantidadeEstoque());

        var disponibilidade = new VerificarDisponibilidadePecaUseCase(pecaRepository)
                .execute(new VerificarDisponibilidadePecaCommand(10L, 20));
        assertFalse(disponibilidade.disponivel());
        assertEquals(15, disponibilidade.quantidadeDisponivel());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> new AtualizarPecaUseCase(pecaRepository).execute(
                        new AtualizarPecaCommand(99L, "x", "x", null, BigDecimal.ONE, null)));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new BaixarEstoqueUseCase(pecaRepository).execute(new BaixarEstoqueCommand(99L, 1)));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new ReporEstoqueUseCase(pecaRepository).execute(new ReporEstoqueCommand(99L, 1)));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new VerificarDisponibilidadePecaUseCase(pecaRepository)
                        .execute(new VerificarDisponibilidadePecaCommand(99L, 1)));
    }

    @Test
    void servicoUseCasesCobremAtualizacaoExclusaoEInexistentes() {
        var servicoRepository = mock(ServicoRepositoryPort.class);
        var servico = Servico.builder()
                .id(7L)
                .nome("Troca")
                .descricao("Troca de oleo")
                .valorUnitario(new BigDecimal("80.00"))
                .tempoEstimadoMinutos(40)
                .build();

        when(servicoRepository.buscarPorId(7L)).thenReturn(Optional.of(servico));
        when(servicoRepository.buscarPorId(99L)).thenReturn(Optional.empty());
        when(servicoRepository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var atualizado = new AtualizarServicoUseCase(servicoRepository).execute(
                new AtualizarServicoCommand(7L, "Troca completa", "Oleo e filtro",
                        new BigDecimal("120.00"), 60));
        assertEquals("Troca completa", atualizado.nome());
        assertEquals(new BigDecimal("120.00"), atualizado.valorUnitario());

        new ExcluirServicoUseCase(servicoRepository).execute(new ExcluirServicoCommand(7L));
        verify(servicoRepository).excluir(7L);

        assertThrows(RecursoNaoEncontradoException.class,
                () -> new AtualizarServicoUseCase(servicoRepository).execute(
                        new AtualizarServicoCommand(99L, "x", "x", BigDecimal.ONE, 1)));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new ExcluirServicoUseCase(servicoRepository).execute(new ExcluirServicoCommand(99L)));
    }

    @Test
    void execucaoEntregaEncerramentoEOrcamentoDiretosCobremSucessoEInexistentes() {
        var execucaoRepository = mock(ExecucaoRepositoryPort.class);
        var entregaRepository = mock(EntregaRepositoryPort.class);
        var encerramentoRepository = mock(EncerramentoRepositoryPort.class);
        var orcamentoRepository = mock(OrcamentoRepositoryPort.class);

        var execucao = Execucao.criar(1L);
        var entrega = Entrega.criar(1L);
        var encerramento = Encerramento.criar(1L);
        var orcamento = Orcamento.gerar(1L, new BigDecimal("200.00"));

        when(execucaoRepository.buscarPorId(1L)).thenReturn(Optional.of(execucao));
        when(execucaoRepository.buscarPorId(99L)).thenReturn(Optional.empty());
        when(execucaoRepository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(entregaRepository.buscarPorId(1L)).thenReturn(Optional.of(entrega));
        when(entregaRepository.buscarPorId(99L)).thenReturn(Optional.empty());
        when(entregaRepository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(encerramentoRepository.buscarPorId(1L)).thenReturn(Optional.of(encerramento));
        when(encerramentoRepository.buscarPorId(99L)).thenReturn(Optional.empty());
        when(encerramentoRepository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(orcamentoRepository.buscarPorId(1L)).thenReturn(Optional.of(orcamento));
        when(orcamentoRepository.buscarPorId(99L)).thenReturn(Optional.empty());
        when(orcamentoRepository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var execucaoComMecanico = new AtualizarMecanicoExecucaoUseCase(execucaoRepository)
                .execute(new AtualizarMecanicoExecucaoCommand(1L, "Carlos"));
        assertEquals("Carlos", execucaoComMecanico.mecanicoNome());

        var execucaoIniciada = new IniciarServicoExecucaoUseCase(execucaoRepository)
                .execute(new IniciarServicoExecucaoCommand(1L));
        assertEquals("EM_ANDAMENTO", execucaoIniciada.status());

        var entregaLiberada = new LiberarVeiculoEntregaUseCase(entregaRepository)
                .execute(new LiberarVeiculoEntregaCommand(1L));
        assertEquals("VEICULO_LIBERADO", entregaLiberada.status());

        var encerrado = new EncerrarOrdemServicoUseCase(encerramentoRepository)
                .execute(new EncerrarOrdemServicoCommand(1L));
        assertEquals("ENCERRADA", encerrado.status());

        var enviado = new EnviarOrcamentoUseCase(orcamentoRepository).execute(new EnviarOrcamentoCommand(1L));
        assertEquals("ENVIADO", enviado.status());

        var aprovado = new AprovarOrcamentoDiretoUseCase(orcamentoRepository)
                .execute(new AprovarOrcamentoDiretoCommand(1L));
        assertEquals("APROVADO", aprovado.status());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> new AtualizarMecanicoExecucaoUseCase(execucaoRepository)
                        .execute(new AtualizarMecanicoExecucaoCommand(99L, "Carlos")));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new IniciarServicoExecucaoUseCase(execucaoRepository)
                        .execute(new IniciarServicoExecucaoCommand(99L)));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new LiberarVeiculoEntregaUseCase(entregaRepository)
                        .execute(new LiberarVeiculoEntregaCommand(99L)));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new EncerrarOrdemServicoUseCase(encerramentoRepository)
                        .execute(new EncerrarOrdemServicoCommand(99L)));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new EnviarOrcamentoUseCase(orcamentoRepository).execute(new EnviarOrcamentoCommand(99L)));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new AprovarOrcamentoDiretoUseCase(orcamentoRepository)
                        .execute(new AprovarOrcamentoDiretoCommand(99L)));
    }

    @Test
    void processarPagamentoCobreAprovacaoRecusaEInexistente() {
        var pagamentoRepository = mock(PagamentoRepositoryPort.class);
        var pagamentoGateway = mock(PagamentoGatewayPort.class);
        var useCase = new ProcessarPagamentoUseCase(pagamentoRepository, pagamentoGateway);
        var pagamentoAprovado = Pagamento.criar(1L, new BigDecimal("100.00"), MetodoPagamento.PIX);
        var pagamentoRecusado = Pagamento.criar(2L, new BigDecimal("150.00"), MetodoPagamento.CARTAO_CREDITO);

        when(pagamentoRepository.buscarPorId(1L)).thenReturn(Optional.of(pagamentoAprovado));
        when(pagamentoRepository.buscarPorId(2L)).thenReturn(Optional.of(pagamentoRecusado));
        when(pagamentoRepository.buscarPorId(99L)).thenReturn(Optional.empty());
        when(pagamentoRepository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(pagamentoGateway.processar(any()))
                .thenReturn(PagamentoGatewayPort.GatewayResponse.aprovado("tx-1", "Aprovado"))
                .thenReturn(PagamentoGatewayPort.GatewayResponse.recusado("tx-2", "Recusado"));

        var aprovado = useCase.execute(new ProcessarPagamentoCommand(1L));
        assertEquals("APROVADO", aprovado.status());
        assertEquals("tx-1", aprovado.transactionId());

        var recusado = useCase.execute(new ProcessarPagamentoCommand(2L));
        assertEquals("RECUSADO", recusado.status());
        assertEquals("tx-2", recusado.transactionId());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> useCase.execute(new ProcessarPagamentoCommand(99L)));
    }

    @Test
    void useCasesSimplesCobremCaminhosNaoEncontrados() {
        var execucaoRepository = mock(ExecucaoRepositoryPort.class);
        var entregaRepository = mock(EntregaRepositoryPort.class);
        var encerramentoRepository = mock(EncerramentoRepositoryPort.class);
        var orcamentoRepository = mock(OrcamentoRepositoryPort.class);
        var pagamentoRepository = mock(PagamentoRepositoryPort.class);
        var pecaRepository = mock(PecaRepositoryPort.class);
        var veiculoRepository = mock(VeiculoRepositoryPort.class);
        var clienteRepository = mock(ClienteRepositoryPort.class);

        assertThrows(RecursoNaoEncontradoException.class,
                () -> new FinalizarServicoExecucaoUseCase(execucaoRepository)
                        .execute(new FinalizarServicoExecucaoCommand(99L)));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new RegistrarEntregaVeiculoUseCase(entregaRepository)
                        .execute(new RegistrarEntregaVeiculoCommand(99L)));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new ExcluirPecaUseCase(pecaRepository).execute(new ExcluirPecaCommand(99L)));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new RecusarPagamentoUseCase(pagamentoRepository)
                        .execute(new RecusarPagamentoCommand(99L, "tx", "Recusado")));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new AtualizarVeiculoUseCase(veiculoRepository, clienteRepository)
                        .execute(new AtualizarVeiculoCommand(99L, "ABC1D23", "Honda", "Civic", 2024, 1L)));

        assertThrows(RecursoNaoEncontradoException.class,
                () -> new BuscarExecucaoPorIdUseCase(execucaoRepository).execute(99L));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new BuscarExecucaoPorOrdemServicoUseCase(execucaoRepository).executeByOrdemServico(99L));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new BuscarEntregaPorIdUseCase(entregaRepository).execute(99L));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new BuscarEntregaPorOrdemServicoUseCase(entregaRepository).executeByOrdemServico(99L));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new BuscarEncerramentoPorIdUseCase(encerramentoRepository).execute(99L));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new BuscarEncerramentoPorOrdemServicoUseCase(encerramentoRepository).executeByOrdemServico(99L));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new BuscarOrcamentoPorIdUseCase(orcamentoRepository).execute(99L));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new BuscarOrcamentoAtivoPorOrdemServicoUseCase(orcamentoRepository).executeByOrdemServico(99L));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new BuscarPagamentoPorIdUseCase(pagamentoRepository).execute(99L));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new BuscarPagamentoPorOrdemServicoUseCase(pagamentoRepository).executeByOrdemServico(99L));

        assertThrows(RecursoNaoEncontradoException.class,
                () -> new ConsultarStatusEncerramentoUseCase(encerramentoRepository).executeStatus(99L));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new ConsultarStatusEntregaUseCase(entregaRepository).executeStatus(99L));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new ConsultarStatusOrcamentoUseCase(orcamentoRepository).executeStatus(99L));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new ConsultarStatusPagamentoUseCase(pagamentoRepository).executeStatus(99L));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> new ValidarValidadeOrcamentoUseCase(orcamentoRepository).executeValidade(99L));
    }
}
