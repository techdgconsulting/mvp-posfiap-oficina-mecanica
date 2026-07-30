package br.com.oficina.application.usecase;

import br.com.oficina.application.command.AtualizarClienteCommand;
import br.com.oficina.application.command.CriarClienteCommand;
import br.com.oficina.application.command.ExcluirClienteCommand;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.out.BuscarEnderecoPorCepPort;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.query.EnderecoResult;
import br.com.oficina.domain.model.Cliente;
import br.com.oficina.domain.valueobject.CpfCnpj;
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
@Feature("Use cases de Clientes")
class ClienteUseCaseTest {

    @Mock
    private ClienteRepositoryPort clienteRepository;

    @Mock
    private BuscarEnderecoPorCepPort buscarEnderecoPorCepPort;

    private CriarClienteUseCase criarClienteUseCase;
    private AtualizarClienteUseCase atualizarClienteUseCase;
    private ExcluirClienteUseCase excluirClienteUseCase;
    private BuscarClientePorIdUseCase buscarClientePorIdUseCase;
    private BuscarClientePorDocumentoUseCase buscarClientePorDocumentoUseCase;
    private ListarClientesUseCase listarClientesUseCase;

    private CriarClienteCommand requestValido;
    private Cliente clienteSalvo;

    @BeforeEach
    void setUp() {
        criarClienteUseCase = new CriarClienteUseCase(clienteRepository, buscarEnderecoPorCepPort);
        atualizarClienteUseCase = new AtualizarClienteUseCase(clienteRepository);
        excluirClienteUseCase = new ExcluirClienteUseCase(clienteRepository);
        buscarClientePorIdUseCase = new BuscarClientePorIdUseCase(clienteRepository);
        buscarClientePorDocumentoUseCase = new BuscarClientePorDocumentoUseCase(clienteRepository);
        listarClientesUseCase = new ListarClientesUseCase(clienteRepository);

        requestValido = new CriarClienteCommand(
                "529.982.247-25", "Joao Silva", "11999998888", "joao@email.com", "01001000", "atendente");

        clienteSalvo = Cliente.builder()
                .id(1L)
                .documento(new CpfCnpj("52998224725"))
                .nome("Joao Silva")
                .telefone("11999998888")
                .email("joao@email.com")
                .build();
    }

    @Test
    @Story("Criar cliente com sucesso")
    void deveCriarClienteComSucesso() {
        when(clienteRepository.existePorDocumento(any())).thenReturn(false);
        when(clienteRepository.salvar(any())).thenReturn(clienteSalvo);
        when(buscarEnderecoPorCepPort.buscarPorCep("01001000"))
            .thenReturn(Optional.of(new EnderecoResult("01001-000", "Praca da Se", "Se", "Sao Paulo", "SP")));

        var resp = criarClienteUseCase.execute(requestValido);

        assertEquals("Joao Silva", resp.nome());
        assertEquals("529.982.247-25", resp.documento());
        verify(clienteRepository).salvar(any(Cliente.class));
        verify(buscarEnderecoPorCepPort).buscarPorCep("01001000");
    }

    @Test
    @Story("Rejeitar cliente duplicado")
    void naoDeveCriarClienteDuplicado() {
        when(clienteRepository.existePorDocumento(any())).thenReturn(true);

        assertThrows(NegocioException.class, () -> criarClienteUseCase.execute(requestValido));
        verify(clienteRepository, never()).salvar(any());
    }

    @Test
    @Story("Buscar cliente por ID")
    void deveBuscarPorId() {
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(clienteSalvo));

        var resp = buscarClientePorIdUseCase.execute(1L);

        assertEquals(1L, resp.id());
        assertEquals("Joao Silva", resp.nome());
    }

    @Test
    @Story("Lancar excecao quando cliente nao encontrado")
    void deveLancarExcecaoQuandoNaoEncontrar() {
        when(clienteRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> buscarClientePorIdUseCase.execute(99L));
    }

    @Test
    @Story("Listar todos os clientes")
    void deveListarTodos() {
        when(clienteRepository.listarTodos()).thenReturn(List.of(clienteSalvo));

        var lista = listarClientesUseCase.execute();

        assertEquals(1, lista.size());
    }

    @Test
    @Story("Atualizar cliente")
    void deveAtualizarCliente() {
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(clienteSalvo));
        when(clienteRepository.salvar(any())).thenReturn(clienteSalvo);

        var req = new AtualizarClienteCommand(1L, "Joao Atualizado", "11888887777", "novo@email.com", "atendente");
        var resp = atualizarClienteUseCase.execute(req);

        assertNotNull(resp);
        verify(clienteRepository).salvar(any());
    }

    @Test
    @Story("Excluir cliente")
    void deveExcluirCliente() {
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(clienteSalvo));
        when(clienteRepository.existeVeiculoVinculado(1L)).thenReturn(false);
        when(clienteRepository.existeOrdemDeServicoVinculada(1L)).thenReturn(false);

        excluirClienteUseCase.execute(new ExcluirClienteCommand(1L, "atendente"));

        verify(clienteRepository).excluir(1L);
    }

    @Test
    @Story("Rejeitar exclusao de cliente inexistente")
    void naoDeveExcluirClienteInexistente() {
        when(clienteRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> excluirClienteUseCase.execute(new ExcluirClienteCommand(99L, "atendente")));
    }

    @Test
    @Story("Rejeitar exclusao de cliente com veiculos vinculados")
    void naoDeveExcluirClienteComVeiculos() {
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(clienteSalvo));
        when(clienteRepository.existeVeiculoVinculado(1L)).thenReturn(true);

        var ex = assertThrows(NegocioException.class,
                () -> excluirClienteUseCase.execute(new ExcluirClienteCommand(1L, "atendente")));
        assertEquals("Nao e possivel excluir cliente com veiculos vinculados", ex.getMessage());
        verify(clienteRepository, never()).excluir(any());
    }

    @Test
    @Story("Rejeitar exclusao de cliente com OS vinculadas")
    void naoDeveExcluirClienteComOrdensDeServico() {
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(clienteSalvo));
        when(clienteRepository.existeVeiculoVinculado(1L)).thenReturn(false);
        when(clienteRepository.existeOrdemDeServicoVinculada(1L)).thenReturn(true);

        var ex = assertThrows(NegocioException.class,
                () -> excluirClienteUseCase.execute(new ExcluirClienteCommand(1L, "atendente")));
        assertEquals("Nao e possivel excluir cliente com ordens de servico vinculadas", ex.getMessage());
        verify(clienteRepository, never()).excluir(any());
    }

    @Test
    @Story("Criar cliente sem CEP")
    void deveCriarClienteSemCep() {
        var reqSemCep = new CriarClienteCommand("529.982.247-25", "Joao Silva", "11999998888", "joao@email.com", null, "atendente");
        when(clienteRepository.existePorDocumento(any())).thenReturn(false);
        when(clienteRepository.salvar(any())).thenReturn(clienteSalvo);

        var resp = criarClienteUseCase.execute(reqSemCep);

        assertNotNull(resp);
        verify(buscarEnderecoPorCepPort, never()).buscarPorCep(any());
    }

    @Test
    @Story("Criar cliente com CEP invalido")
    void deveCriarClienteComCepInvalido() {
        var reqCepInvalido = new CriarClienteCommand("529.982.247-25", "Joao Silva", "11999998888", "joao@email.com", "00000000", "atendente");
        when(clienteRepository.existePorDocumento(any())).thenReturn(false);
        when(clienteRepository.salvar(any())).thenReturn(clienteSalvo);
        when(buscarEnderecoPorCepPort.buscarPorCep("00000000")).thenReturn(Optional.empty());

        var resp = criarClienteUseCase.execute(reqCepInvalido);

        assertNotNull(resp);
        verify(buscarEnderecoPorCepPort).buscarPorCep("00000000");
    }

    @Test
    @Story("Buscar cliente por documento")
    void deveBuscarPorDocumento() {
        when(clienteRepository.buscarPorDocumento("52998224725")).thenReturn(Optional.of(clienteSalvo));

        var resp = buscarClientePorDocumentoUseCase.execute("529.982.247-25");

        assertEquals("Joao Silva", resp.nome());
    }

    @Test
    @Story("Rejeitar busca por documento inexistente")
    void naoDeveBuscarPorDocumentoInexistente() {
        when(clienteRepository.buscarPorDocumento("52998224725")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> buscarClientePorDocumentoUseCase.execute("529.982.247-25"));
    }

    @Test
    @Story("Rejeitar atualizacao de cliente inexistente")
    void naoDeveAtualizarClienteInexistente() {
        when(clienteRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        var req = new AtualizarClienteCommand(99L, "Joao", "11999998888", "joao@email.com", "atendente");
        assertThrows(RecursoNaoEncontradoException.class, () -> atualizarClienteUseCase.execute(req));
    }
}
