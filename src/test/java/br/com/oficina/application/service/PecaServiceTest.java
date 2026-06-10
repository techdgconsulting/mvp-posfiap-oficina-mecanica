package br.com.oficina.application.service;

import br.com.oficina.application.dto.PecaRequest;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.domain.estoque.Peca;
import br.com.oficina.domain.estoque.PecaRepository;
import br.com.oficina.domain.estoque.vo.Quantidade;
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
@Epic("Gestão de Estoque")
@Feature("Serviço de Peças")
class PecaServiceTest {

    @Mock
    private PecaRepository repo;

    @InjectMocks
    private PecaService service;

    private Peca peca;

    @BeforeEach
    void prepara() {
        peca = Peca.builder()
                .id(1L)
                .nome("Filtro de Ar")
                .descricao("Filtro motor")
                .quantidadeEstoque(new Quantidade(20))
                .valorUnitario(new BigDecimal("45.00"))
                .build();
    }

    @Test
    @Story("Criar peça no estoque")
    void criarPeca() {
        when(repo.salvar(any())).thenReturn(peca);

        var req = new PecaRequest("Filtro de Ar", "Filtro motor", 20, new BigDecimal("45.00"), 10);
        var resp = service.criar(req);

        assertEquals("Filtro de Ar", resp.nome());
        assertEquals(20, resp.quantidadeEstoque());
    }

    @Test
    @Story("Criar peça sem estoque mínimo")
    void criarPecaSemEstoqueMinimo() {
        when(repo.salvar(any())).thenReturn(peca);

        // estoqueMinimo null — deve criar sem setar estoqueMinimo no builder
        var req = new PecaRequest("Filtro de Ar", "Filtro motor", 20, new BigDecimal("45.00"), null);
        var resp = service.criar(req);

        assertNotNull(resp);
        verify(repo).salvar(any());
    }

    @Test
    @Story("Buscar peça por ID")
    void buscar() {
        when(repo.buscarPorId(1L)).thenReturn(Optional.of(peca));
        assertEquals("Filtro de Ar", service.buscarPorId(1L).nome());
    }

    @Test
    @Story("Rejeitar busca de peça inexistente")
    void buscarNaoExiste() {
        when(repo.buscarPorId(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    @Test
    @Story("Listar todas as peças")
    void listarTodas() {
        when(repo.listarTodas()).thenReturn(List.of(peca));
        assertEquals(1, service.listarTodas().size());
    }

    @Test
    @Story("Listar peças com estoque baixo")
    void listarEstoqueBaixo() {
        when(repo.listarComEstoqueBaixo()).thenReturn(List.of());
        assertTrue(service.listarEstoqueBaixo().isEmpty());
    }

    @Test
    @Story("Atualizar peça com nova quantidade")
    void atualizarComNovaQuantidade() {
        when(repo.buscarPorId(1L)).thenReturn(Optional.of(peca));
        when(repo.salvar(any())).thenReturn(peca);

        var req = new PecaRequest("Filtro Novo", "Desc", 50, new BigDecimal("55.00"), 10);
        var resp = service.atualizar(1L, req);
        assertNotNull(resp);
    }

    @Test
    @Story("Atualizar peça sem alterar quantidade")
    void atualizarSemQuantidade() {
        when(repo.buscarPorId(1L)).thenReturn(Optional.of(peca));
        when(repo.salvar(any())).thenReturn(peca);

        // quantidadeEstoque null -> não altera
        var req = new PecaRequest("Filtro", "Desc", null, new BigDecimal("55.00"), null);
        service.atualizar(1L, req);
        verify(repo).salvar(any());
    }

    @Test
    @Story("Excluir peça do estoque")
    void excluir() {
        when(repo.buscarPorId(1L)).thenReturn(Optional.of(peca));
        service.excluir(1L);
        verify(repo).excluir(1L);
    }

    @Test
    @Story("Repor estoque de peça")
    void reporEstoque() {
        when(repo.buscarPorId(1L)).thenReturn(Optional.of(peca));
        when(repo.salvar(any())).thenReturn(peca);

        var resp = service.reporEstoque(1L, 10);
        assertNotNull(resp);
        verify(repo).salvar(any());
    }
}
