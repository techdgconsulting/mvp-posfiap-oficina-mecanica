package br.com.oficina.application.service;

import br.com.oficina.application.dto.VeiculoRequest;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.domain.atendimento.cliente.Cliente;
import br.com.oficina.domain.atendimento.cliente.ClienteRepository;
import br.com.oficina.domain.atendimento.cliente.vo.CpfCnpj;
import br.com.oficina.domain.atendimento.veiculo.Veiculo;
import br.com.oficina.domain.atendimento.veiculo.VeiculoRepository;
import br.com.oficina.domain.atendimento.veiculo.vo.Placa;
import br.com.oficina.domain.ordemservico.OrdemDeServico;
import br.com.oficina.domain.ordemservico.OrdemDeServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

@ExtendWith(MockitoExtension.class)
@Epic("Atendimento ao Cliente")
@Feature("Serviço de Veículos")
class VeiculoServiceTest {

    @Mock private VeiculoRepository veiculoRepo;
    @Mock private ClienteRepository clienteRepo;
    @Mock private OrdemDeServicoRepository ordemDeServicoRepository;

    @InjectMocks
    private VeiculoService service;

    private Cliente cliente;
    private Veiculo veiculo;

    @BeforeEach
    void montaCenario() {
        cliente = Cliente.builder()
                .id(1L)
                .documento(new CpfCnpj("52998224725"))
                .nome("Ana")
                .build();

        veiculo = Veiculo.builder()
                .id(10L)
                .placa(new Placa("ABC1D23"))
                .marca("Toyota").modelo("Corolla").ano(2021)
                .cliente(cliente)
                .build();
    }

    @Test
    @Story("Criar veículo para cliente")
    void criarVeiculo() {
        when(clienteRepo.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepo.salvar(any())).thenReturn(veiculo);

        var req = new VeiculoRequest("ABC1D23", "Toyota", "Corolla", 2021, 1L);
        var resp = service.criar(req);

        assertEquals("ABC1D23", resp.placa());
        assertEquals("Toyota", resp.marca());
    }

    @Test
    @Story("Rejeitar criação sem cliente")
    void criarSemCliente_deveFalhar() {
        when(clienteRepo.buscarPorId(99L)).thenReturn(Optional.empty());
        var req = new VeiculoRequest("ABC1D23", "Fiat", "Uno", 2020, 99L);

        assertThrows(RecursoNaoEncontradoException.class, () -> service.criar(req));
    }

    @Test
    @Story("Rejeitar veículo com placa duplicada")
    void naoDeveCriarVeiculoComPlacaDuplicada() {
        when(clienteRepo.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepo.buscarPorPlaca("ABC1D23")).thenReturn(Optional.of(veiculo));

        var req = new VeiculoRequest("ABC1D23", "Honda", "Civic", 2022, 1L);

        var ex = assertThrows(NegocioException.class, () -> service.criar(req));
        assertTrue(ex.getMessage().contains("ABC1D23"));
        verify(veiculoRepo, never()).salvar(any());
    }

    @Test
    @Story("Buscar veículo por ID")
    void buscarPorId() {
        when(veiculoRepo.buscarPorId(10L)).thenReturn(Optional.of(veiculo));
        var resp = service.buscarPorId(10L);
        assertEquals(10L, resp.id());
    }

    @Test
    @Story("Rejeitar busca de veículo inexistente")
    void buscarInexistente() {
        when(veiculoRepo.buscarPorId(5L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(5L));
    }

    @Test
    @Story("Listar todos os veículos")
    void listarTodos() {
        when(veiculoRepo.listarTodos()).thenReturn(List.of(veiculo));
        assertEquals(1, service.listarTodos().size());
    }

    @Test
    @Story("Listar veículos por cliente")
    void listarPorCliente() {
        when(veiculoRepo.listarPorCliente(1L)).thenReturn(List.of(veiculo));
        assertEquals(1, service.listarPorCliente(1L).size());
    }

    @Test
    @Story("Atualizar veículo")
    void atualizar() {
        when(veiculoRepo.buscarPorId(10L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepo.salvar(any())).thenReturn(veiculo);

        var req = new VeiculoRequest("ABC1D23", "Honda", "Civic", 2023, 1L);
        var resp = service.atualizar(10L, req);
        assertNotNull(resp);
        verify(veiculoRepo).salvar(any());
    }

    @Test
    @Story("Excluir veículo")
    void excluir() {
        when(veiculoRepo.buscarPorId(10L)).thenReturn(Optional.of(veiculo));
        when(ordemDeServicoRepository.listarPorVeiculo(10L)).thenReturn(List.of());
        service.excluir(10L);
        verify(veiculoRepo).excluir(10L);
    }

    @Test
    @Story("Rejeitar exclusao de veiculo com OS vinculada")
    void naoDeveExcluirVeiculoComOrdemDeServicoVinculada() {
        when(veiculoRepo.buscarPorId(10L)).thenReturn(Optional.of(veiculo));
        when(ordemDeServicoRepository.listarPorVeiculo(10L))
                .thenReturn(List.of(mock(OrdemDeServico.class)));

        assertThrows(NegocioException.class, () -> service.excluir(10L));
        verify(veiculoRepo, never()).excluir(any());
    }
}
