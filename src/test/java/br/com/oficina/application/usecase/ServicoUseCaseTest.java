package br.com.oficina.application.usecase;

import br.com.oficina.application.command.AtualizarServicoCommand;
import br.com.oficina.application.command.CriarServicoCommand;
import br.com.oficina.application.command.ExcluirServicoCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.out.ServicoRepositoryPort;
import br.com.oficina.domain.model.Servico;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Epic("Catalogo de Servicos")
@Feature("Use cases de Catalogo")
class ServicoUseCaseTest {

    @Mock
    private ServicoRepositoryPort repo;

    private CriarServicoUseCase criarServicoUseCase;
    private BuscarServicoPorIdUseCase buscarServicoPorIdUseCase;
    private ListarServicosUseCase listarServicosUseCase;
    private AtualizarServicoUseCase atualizarServicoUseCase;
    private ExcluirServicoUseCase excluirServicoUseCase;

    private Servico servico;

    @BeforeEach
    void setUp() {
        criarServicoUseCase = new CriarServicoUseCase(repo);
        buscarServicoPorIdUseCase = new BuscarServicoPorIdUseCase(repo);
        listarServicosUseCase = new ListarServicosUseCase(repo);
        atualizarServicoUseCase = new AtualizarServicoUseCase(repo);
        excluirServicoUseCase = new ExcluirServicoUseCase(repo);
        servico = new Servico("Troca de oleo", "Troca completa", new BigDecimal("150.00"), 30);
    }

    @Test
    @Story("Criar servico no catalogo")
    void deveCriar() {
        when(repo.salvar(any())).thenReturn(servico);

        var command = new CriarServicoCommand("Troca de oleo", "Troca completa", new BigDecimal("150.00"), 30);
        var resp = criarServicoUseCase.execute(command);

        assertEquals("Troca de oleo", resp.nome());
        assertEquals(new BigDecimal("150.00"), resp.valorUnitario());
    }

    @Test
    @Story("Buscar servico por ID")
    void deveBuscarPorId() {
        when(repo.buscarPorId(1L)).thenReturn(Optional.of(servico));

        var resp = buscarServicoPorIdUseCase.execute(1L);

        assertEquals("Troca de oleo", resp.nome());
    }

    @Test
    void buscarInexistente_lanca() {
        when(repo.buscarPorId(42L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> buscarServicoPorIdUseCase.execute(42L));
    }

    @Test
    @Story("Listar servicos do catalogo")
    void deveListar() {
        when(repo.listarTodos()).thenReturn(List.of(servico));

        var lista = listarServicosUseCase.execute();

        assertFalse(lista.isEmpty());
    }

    @Test
    @Story("Atualizar servico do catalogo")
    void deveAtualizar() {
        when(repo.buscarPorId(1L)).thenReturn(Optional.of(servico));
        when(repo.salvar(any())).thenReturn(servico);

        var command = new AtualizarServicoCommand(1L, "Alinhamento", null, new BigDecimal("80"), 20);

        assertNotNull(atualizarServicoUseCase.execute(command));
    }

    @Test
    @Story("Excluir servico do catalogo")
    void deveExcluir() {
        when(repo.buscarPorId(1L)).thenReturn(Optional.of(servico));

        excluirServicoUseCase.execute(new ExcluirServicoCommand(1L));

        verify(repo).excluir(1L);
    }
}
