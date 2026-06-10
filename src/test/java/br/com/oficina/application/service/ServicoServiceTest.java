package br.com.oficina.application.service;

import br.com.oficina.application.dto.ServicoRequest;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.domain.servico.Servico;
import br.com.oficina.domain.servico.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

@ExtendWith(MockitoExtension.class)
@Epic("Catálogo de Serviços")
@Feature("Serviço de Catálogo")
class ServicoServiceTest {

    @Mock
    private ServicoRepository repo;

    @InjectMocks
    private ServicoService service;

    private Servico servico;

    @BeforeEach
    void setUp() {
        servico = new Servico("Troca de óleo", "Troca completa", new BigDecimal("150.00"), 30);
        // setar id via reflection seria overkill, vamos testar sem
    }

    @Test
    @Story("Criar serviço no catálogo")
    void deveCriar() {
        when(repo.salvar(any())).thenReturn(servico);
        var req = new ServicoRequest("Troca de óleo", "Troca completa", new BigDecimal("150.00"), 30);
        var resp = service.criar(req);

        assertEquals("Troca de óleo", resp.nome());
        assertEquals(new BigDecimal("150.00"), resp.valorUnitario());
    }

    @Test
    @Story("Buscar serviço por ID")
    void deveBuscarPorId() {
        when(repo.buscarPorId(1L)).thenReturn(Optional.of(servico));
        var resp = service.buscarPorId(1L);
        assertEquals("Troca de óleo", resp.nome());
    }

    @Test
    void buscarInexistente_lanca() {
        when(repo.buscarPorId(42L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(42L));
    }

    @Test
    @Story("Listar serviços do catálogo")
    void deveListar() {
        when(repo.listarTodos()).thenReturn(List.of(servico));
        var lista = service.listarTodos();
        assertFalse(lista.isEmpty());
    }

    @Test
    @Story("Atualizar serviço do catálogo")
    void deveAtualizar() {
        when(repo.buscarPorId(1L)).thenReturn(Optional.of(servico));
        when(repo.salvar(any())).thenReturn(servico);

        var req = new ServicoRequest("Alinhamento", null, new BigDecimal("80"), 20);
        assertNotNull(service.atualizar(1L, req));
    }

    @Test
    @Story("Excluir serviço do catálogo")
    void deveExcluir() {
        when(repo.buscarPorId(1L)).thenReturn(Optional.of(servico));
        service.excluir(1L);
        verify(repo).excluir(1L);
    }
}
