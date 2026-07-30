package br.com.oficina.application.usecase;

import br.com.oficina.application.command.AtualizarPecaCommand;
import br.com.oficina.application.command.BaixarEstoqueCommand;
import br.com.oficina.application.command.CriarPecaCommand;
import br.com.oficina.application.command.ExcluirPecaCommand;
import br.com.oficina.application.command.ReporEstoqueCommand;
import br.com.oficina.application.command.VerificarDisponibilidadePecaCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.out.PecaRepositoryPort;
import br.com.oficina.domain.model.Peca;
import br.com.oficina.domain.valueobject.Quantidade;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Epic("Gestao de Estoque")
@Feature("Use cases de Pecas")
class PecaUseCaseTest {

    @Mock
    private PecaRepositoryPort repo;

    private CriarPecaUseCase criarPecaUseCase;
    private BuscarPecaPorIdUseCase buscarPecaPorIdUseCase;
    private ListarPecasUseCase listarPecasUseCase;
    private ListarPecasComEstoqueBaixoUseCase listarPecasComEstoqueBaixoUseCase;
    private AtualizarPecaUseCase atualizarPecaUseCase;
    private ExcluirPecaUseCase excluirPecaUseCase;
    private ReporEstoqueUseCase reporEstoqueUseCase;
    private BaixarEstoqueUseCase baixarEstoqueUseCase;
    private VerificarDisponibilidadePecaUseCase verificarDisponibilidadePecaUseCase;

    private Peca peca;

    @BeforeEach
    void prepara() {
        criarPecaUseCase = new CriarPecaUseCase(repo);
        buscarPecaPorIdUseCase = new BuscarPecaPorIdUseCase(repo);
        listarPecasUseCase = new ListarPecasUseCase(repo);
        listarPecasComEstoqueBaixoUseCase = new ListarPecasComEstoqueBaixoUseCase(repo);
        atualizarPecaUseCase = new AtualizarPecaUseCase(repo);
        excluirPecaUseCase = new ExcluirPecaUseCase(repo);
        reporEstoqueUseCase = new ReporEstoqueUseCase(repo);
        baixarEstoqueUseCase = new BaixarEstoqueUseCase(repo);
        verificarDisponibilidadePecaUseCase = new VerificarDisponibilidadePecaUseCase(repo);

        peca = Peca.builder()
                .id(1L)
                .nome("Filtro de Ar")
                .descricao("Filtro motor")
                .quantidadeEstoque(new Quantidade(20))
                .valorUnitario(new BigDecimal("45.00"))
                .build();
    }

    @Test
    @Story("Criar peca no estoque")
    void criarPeca() {
        when(repo.salvar(any())).thenReturn(peca);

        var command = new CriarPecaCommand("Filtro de Ar", "Filtro motor", 20, new BigDecimal("45.00"), 10);
        var resp = criarPecaUseCase.execute(command);

        assertEquals("Filtro de Ar", resp.nome());
        assertEquals(20, resp.quantidadeEstoque());
    }

    @Test
    @Story("Criar peca sem estoque minimo")
    void criarPecaSemEstoqueMinimo() {
        when(repo.salvar(any())).thenReturn(peca);

        var command = new CriarPecaCommand("Filtro de Ar", "Filtro motor", 20, new BigDecimal("45.00"), null);
        var resp = criarPecaUseCase.execute(command);

        assertNotNull(resp);
        verify(repo).salvar(any());
    }

    @Test
    @Story("Buscar peca por ID")
    void buscar() {
        when(repo.buscarPorId(1L)).thenReturn(Optional.of(peca));

        assertEquals("Filtro de Ar", buscarPecaPorIdUseCase.execute(1L).nome());
    }

    @Test
    @Story("Rejeitar busca de peca inexistente")
    void buscarNaoExiste() {
        when(repo.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> buscarPecaPorIdUseCase.execute(99L));
    }

    @Test
    @Story("Listar todas as pecas")
    void listarTodas() {
        when(repo.listarTodas()).thenReturn(List.of(peca));

        assertEquals(1, listarPecasUseCase.execute().size());
    }

    @Test
    @Story("Listar pecas com estoque baixo")
    void listarEstoqueBaixo() {
        when(repo.listarComEstoqueBaixo()).thenReturn(List.of());

        assertTrue(listarPecasComEstoqueBaixoUseCase.executeEstoqueBaixo().isEmpty());
    }

    @Test
    @Story("Atualizar peca com nova quantidade")
    void atualizarComNovaQuantidade() {
        when(repo.buscarPorId(1L)).thenReturn(Optional.of(peca));
        when(repo.salvar(any())).thenReturn(peca);

        var command = new AtualizarPecaCommand(1L, "Filtro Novo", "Desc", 50, new BigDecimal("55.00"), 10);
        var resp = atualizarPecaUseCase.execute(command);

        assertNotNull(resp);
    }

    @Test
    @Story("Atualizar peca sem alterar quantidade")
    void atualizarSemQuantidade() {
        when(repo.buscarPorId(1L)).thenReturn(Optional.of(peca));
        when(repo.salvar(any())).thenReturn(peca);

        var command = new AtualizarPecaCommand(1L, "Filtro", "Desc", null, new BigDecimal("55.00"), null);
        atualizarPecaUseCase.execute(command);

        verify(repo).salvar(any());
    }

    @Test
    @Story("Excluir peca do estoque")
    void excluir() {
        when(repo.buscarPorId(1L)).thenReturn(Optional.of(peca));

        excluirPecaUseCase.execute(new ExcluirPecaCommand(1L));

        verify(repo).excluir(1L);
    }

    @Test
    @Story("Repor estoque de peca")
    void reporEstoque() {
        when(repo.buscarPorId(1L)).thenReturn(Optional.of(peca));
        when(repo.salvar(any())).thenReturn(peca);

        var resp = reporEstoqueUseCase.execute(new ReporEstoqueCommand(1L, 10));

        assertNotNull(resp);
        verify(repo).salvar(any());
    }

    @Test
    @Story("Baixar estoque de peca")
    void baixarEstoque() {
        when(repo.buscarPorId(1L)).thenReturn(Optional.of(peca));
        when(repo.salvar(any())).thenReturn(peca);

        var resp = baixarEstoqueUseCase.execute(new BaixarEstoqueCommand(1L, 5));

        assertNotNull(resp);
        verify(repo).salvar(any());
    }

    @Test
    @Story("Verificar disponibilidade")
    void verificarDisponibilidade() {
        when(repo.buscarPorId(1L)).thenReturn(Optional.of(peca));

        var resp = verificarDisponibilidadePecaUseCase.execute(new VerificarDisponibilidadePecaCommand(1L, 5));

        assertTrue(resp.disponivel());
        assertEquals(20, resp.quantidadeDisponivel());
    }
}
