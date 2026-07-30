package br.com.oficina.application.usecase;

import br.com.oficina.application.command.AtualizarVeiculoCommand;
import br.com.oficina.application.command.CriarVeiculoCommand;
import br.com.oficina.application.command.ExcluirVeiculoCommand;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.port.out.VeiculoRepositoryPort;
import br.com.oficina.domain.model.Cliente;
import br.com.oficina.domain.model.Veiculo;
import br.com.oficina.domain.valueobject.CpfCnpj;
import br.com.oficina.domain.valueobject.Placa;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Epic("Atendimento ao Cliente")
@Feature("Use cases de Veiculos")
class VeiculoUseCaseTest {

    @Mock private VeiculoRepositoryPort veiculoRepo;
    @Mock private ClienteRepositoryPort clienteRepo;

    private CriarVeiculoUseCase criarVeiculoUseCase;
    private BuscarVeiculoPorIdUseCase buscarVeiculoPorIdUseCase;
    private ListarVeiculosUseCase listarVeiculosUseCase;
    private ListarVeiculosPorClienteUseCase listarVeiculosPorClienteUseCase;
    private AtualizarVeiculoUseCase atualizarVeiculoUseCase;
    private ExcluirVeiculoUseCase excluirVeiculoUseCase;

    private Cliente cliente;
    private Veiculo veiculo;

    @BeforeEach
    void montaCenario() {
        criarVeiculoUseCase = new CriarVeiculoUseCase(veiculoRepo, clienteRepo);
        buscarVeiculoPorIdUseCase = new BuscarVeiculoPorIdUseCase(veiculoRepo, clienteRepo);
        listarVeiculosUseCase = new ListarVeiculosUseCase(veiculoRepo, clienteRepo);
        listarVeiculosPorClienteUseCase = new ListarVeiculosPorClienteUseCase(veiculoRepo, clienteRepo);
        atualizarVeiculoUseCase = new AtualizarVeiculoUseCase(veiculoRepo, clienteRepo);
        excluirVeiculoUseCase = new ExcluirVeiculoUseCase(veiculoRepo);

        cliente = Cliente.builder()
                .id(1L)
                .documento(new CpfCnpj("52998224725"))
                .nome("Ana")
                .build();

        veiculo = Veiculo.builder()
                .id(10L)
                .placa(new Placa("ABC1D23"))
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2021)
                .clienteId(1L)
                .cliente(cliente)
                .build();
    }

    @Test
    @Story("Criar veiculo para cliente")
    void criarVeiculo() {
        when(clienteRepo.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepo.salvar(any())).thenReturn(veiculo);

        var req = new CriarVeiculoCommand("ABC1D23", "Toyota", "Corolla", 2021, 1L);
        var resp = criarVeiculoUseCase.execute(req);

        assertEquals("ABC1D23", resp.placa());
        assertEquals("Toyota", resp.marca());
    }

    @Test
    @Story("Rejeitar criacao sem cliente")
    void criarSemCliente_deveFalhar() {
        when(clienteRepo.buscarPorId(99L)).thenReturn(Optional.empty());
        var req = new CriarVeiculoCommand("ABC1D23", "Fiat", "Uno", 2020, 99L);

        assertThrows(RecursoNaoEncontradoException.class, () -> criarVeiculoUseCase.execute(req));
    }

    @Test
    @Story("Rejeitar veiculo com placa duplicada")
    void naoDeveCriarVeiculoComPlacaDuplicada() {
        when(clienteRepo.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepo.buscarPorPlaca("ABC1D23")).thenReturn(Optional.of(veiculo));

        var req = new CriarVeiculoCommand("ABC1D23", "Honda", "Civic", 2022, 1L);

        var ex = assertThrows(NegocioException.class, () -> criarVeiculoUseCase.execute(req));
        assertTrue(ex.getMessage().contains("ABC1D23"));
        verify(veiculoRepo, never()).salvar(any());
    }

    @Test
    @Story("Buscar veiculo por ID")
    void buscarPorId() {
        when(veiculoRepo.buscarPorId(10L)).thenReturn(Optional.of(veiculo));
        when(clienteRepo.buscarPorId(1L)).thenReturn(Optional.of(cliente));

        var resp = buscarVeiculoPorIdUseCase.execute(10L);

        assertEquals(10L, resp.id());
    }

    @Test
    @Story("Rejeitar busca de veiculo inexistente")
    void buscarInexistente() {
        when(veiculoRepo.buscarPorId(5L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> buscarVeiculoPorIdUseCase.execute(5L));
    }

    @Test
    @Story("Listar todos os veiculos")
    void listarTodos() {
        when(veiculoRepo.listarTodos()).thenReturn(List.of(veiculo));
        when(clienteRepo.buscarPorId(1L)).thenReturn(Optional.of(cliente));

        assertEquals(1, listarVeiculosUseCase.execute().size());
    }

    @Test
    @Story("Listar veiculos por cliente")
    void listarPorCliente() {
        when(veiculoRepo.listarPorCliente(1L)).thenReturn(List.of(veiculo));
        when(clienteRepo.buscarPorId(1L)).thenReturn(Optional.of(cliente));

        assertEquals(1, listarVeiculosPorClienteUseCase.execute(1L).size());
    }

    @Test
    @Story("Atualizar veiculo")
    void atualizar() {
        when(veiculoRepo.buscarPorId(10L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepo.salvar(any())).thenReturn(veiculo);
        when(clienteRepo.buscarPorId(1L)).thenReturn(Optional.of(cliente));

        var req = new AtualizarVeiculoCommand(10L, "ABC1D23", "Honda", "Civic", 2023, 1L);
        var resp = atualizarVeiculoUseCase.execute(req);

        assertNotNull(resp);
        verify(veiculoRepo).salvar(any());
    }

    @Test
    @Story("Excluir veiculo")
    void excluir() {
        when(veiculoRepo.buscarPorId(10L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepo.existeOrdemDeServicoVinculada(10L)).thenReturn(false);

        excluirVeiculoUseCase.execute(new ExcluirVeiculoCommand(10L));

        verify(veiculoRepo).excluir(10L);
    }

    @Test
    @Story("Rejeitar exclusao de veiculo com OS vinculada")
    void naoDeveExcluirVeiculoComOrdemDeServicoVinculada() {
        when(veiculoRepo.buscarPorId(10L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepo.existeOrdemDeServicoVinculada(10L)).thenReturn(true);

        assertThrows(NegocioException.class, () -> excluirVeiculoUseCase.execute(new ExcluirVeiculoCommand(10L)));
        verify(veiculoRepo, never()).excluir(any());
    }
}
