package br.com.oficina.application.service;

import br.com.oficina.application.dto.ClienteRequest;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.domain.atendimento.cliente.Cliente;
import br.com.oficina.domain.atendimento.cliente.ClienteRepository;
import br.com.oficina.domain.atendimento.cliente.vo.CpfCnpj;
import br.com.oficina.domain.atendimento.veiculo.VeiculoRepository;
import br.com.oficina.domain.ordemservico.OrdemDeServicoRepository;
import br.com.oficina.infrastructure.client.ViaCepClient;
import br.com.oficina.infrastructure.client.ViaCepResponse;
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
@Feature("Serviço de Clientes")
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private OrdemDeServicoRepository ordemDeServicoRepository;

    @Mock
    private ViaCepClient viaCepClient;

    @InjectMocks
    private ClienteService service;

    private ClienteRequest requestValido;
    private Cliente clienteSalvo;

    @BeforeEach
    void setUp() {
        requestValido = new ClienteRequest("529.982.247-25", "João Silva", "11999998888", "joao@email.com", "01001000");

        clienteSalvo = Cliente.builder()
                .id(1L)
                .documento(new CpfCnpj("52998224725"))
                .nome("João Silva")
                .telefone("11999998888")
                .email("joao@email.com")
                .build();
    }

    @Test
    @Story("Criar cliente com sucesso")
    void deveCriarClienteComSucesso() {
        when(clienteRepository.existePorDocumento(any())).thenReturn(false);
        when(clienteRepository.salvar(any())).thenReturn(clienteSalvo);
        when(viaCepClient.buscarPorCep("01001000"))
            .thenReturn(Optional.of(new ViaCepResponse("01001-000", "Praça da Sé", "Sé", "São Paulo", "SP", null)));

        var resp = service.criar(requestValido);

        assertEquals("João Silva", resp.nome());
        assertEquals("529.982.247-25", resp.documento());
        verify(clienteRepository).salvar(any(Cliente.class));
        verify(viaCepClient).buscarPorCep("01001000");
    }

    @Test
    @Story("Rejeitar cliente duplicado")
    void naoDeveCriarClienteDuplicado() {
        when(clienteRepository.existePorDocumento(any())).thenReturn(true);

        assertThrows(NegocioException.class, () -> service.criar(requestValido));
        verify(clienteRepository, never()).salvar(any());
    }

    @Test
    @Story("Buscar cliente por ID")
    void deveBuscarPorId() {
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(clienteSalvo));

        var resp = service.buscarPorId(1L);

        assertEquals(1L, resp.id());
        assertEquals("João Silva", resp.nome());
    }

    @Test
    @Story("Lançar exceção quando cliente não encontrado")
    void deveLancarExcecaoQuandoNaoEncontrar() {
        when(clienteRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    @Test
    @Story("Listar todos os clientes")
    void deveListarTodos() {
        when(clienteRepository.listarTodos()).thenReturn(List.of(clienteSalvo));

        var lista = service.listarTodos();

        assertEquals(1, lista.size());
    }

    @Test
    @Story("Atualizar cliente")
    void deveAtualizarCliente() {
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(clienteSalvo));
        when(clienteRepository.salvar(any())).thenReturn(clienteSalvo);

        var req = new ClienteRequest("52998224725", "João Atualizado", "11888887777", "novo@email.com", null);
        var resp = service.atualizar(1L, req);

        assertNotNull(resp);
        verify(clienteRepository).salvar(any());
    }

    @Test
    @Story("Excluir cliente")
    void deveExcluirCliente() {
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(clienteSalvo));
        when(veiculoRepository.listarPorCliente(1L)).thenReturn(List.of());
        when(ordemDeServicoRepository.listarPorCliente(1L)).thenReturn(List.of());

        service.excluir(1L);

        verify(clienteRepository).excluir(1L);
    }

    @Test
    @Story("Rejeitar exclusão de cliente inexistente")
    void naoDeveExcluirClienteInexistente() {
        when(clienteRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.excluir(99L));
    }

    @Test
    @Story("Rejeitar exclusão de cliente com veículos vinculados")
    void naoDeveExcluirClienteComVeiculos() {
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(clienteSalvo));
        when(veiculoRepository.listarPorCliente(1L)).thenReturn(List.of(mock(br.com.oficina.domain.atendimento.veiculo.Veiculo.class)));

        var ex = assertThrows(NegocioException.class, () -> service.excluir(1L));
        assertEquals("Não é possível excluir cliente com veículos vinculados", ex.getMessage());
        verify(clienteRepository, never()).excluir(any());
    }

    @Test
    @Story("Rejeitar exclusão de cliente com OS vinculadas")
    void naoDeveExcluirClienteComOrdensDeServico() {
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(clienteSalvo));
        when(veiculoRepository.listarPorCliente(1L)).thenReturn(List.of());
        when(ordemDeServicoRepository.listarPorCliente(1L)).thenReturn(List.of(mock(br.com.oficina.domain.ordemservico.OrdemDeServico.class)));

        var ex = assertThrows(NegocioException.class, () -> service.excluir(1L));
        assertEquals("Não é possível excluir cliente com ordens de serviço vinculadas", ex.getMessage());
        verify(clienteRepository, never()).excluir(any());
    }

    @Test
    @Story("Criar cliente sem CEP")
    void deveCriarClienteSemCep() {
        var reqSemCep = new ClienteRequest("529.982.247-25", "João Silva", "11999998888", "joao@email.com", null);
        when(clienteRepository.existePorDocumento(any())).thenReturn(false);
        when(clienteRepository.salvar(any())).thenReturn(clienteSalvo);

        var resp = service.criar(reqSemCep);

        assertNotNull(resp);
        verify(viaCepClient, never()).buscarPorCep(any());
    }

    @Test
    @Story("Criar cliente com CEP inválido (sem endereço)")
    void deveCriarClienteComCepInvalido() {
        var reqCepInvalido = new ClienteRequest("529.982.247-25", "João Silva", "11999998888", "joao@email.com", "00000000");
        when(clienteRepository.existePorDocumento(any())).thenReturn(false);
        when(clienteRepository.salvar(any())).thenReturn(clienteSalvo);
        when(viaCepClient.buscarPorCep("00000000")).thenReturn(Optional.empty());

        var resp = service.criar(reqCepInvalido);

        assertNotNull(resp);
        verify(viaCepClient).buscarPorCep("00000000");
    }

    @Test
    @Story("Buscar cliente por documento")
    void deveBuscarPorDocumento() {
        when(clienteRepository.buscarPorDocumento("52998224725")).thenReturn(Optional.of(clienteSalvo));

        var resp = service.buscarPorDocumento("529.982.247-25");

        assertEquals("João Silva", resp.nome());
    }

    @Test
    @Story("Rejeitar busca por documento inexistente")
    void naoDeveBuscarPorDocumentoInexistente() {
        when(clienteRepository.buscarPorDocumento("52998224725")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorDocumento("529.982.247-25"));
    }

    @Test
    @Story("Rejeitar atualização de cliente inexistente")
    void naoDeveAtualizarClienteInexistente() {
        when(clienteRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        var req = new ClienteRequest("52998224725", "João", "11999998888", "joao@email.com", null);
        assertThrows(RecursoNaoEncontradoException.class, () -> service.atualizar(99L, req));
    }
}
