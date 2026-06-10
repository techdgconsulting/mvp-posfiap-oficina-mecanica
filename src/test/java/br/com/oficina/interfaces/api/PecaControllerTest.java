package br.com.oficina.interfaces.api;

import br.com.oficina.application.dto.PecaResponse;
import br.com.oficina.application.service.PecaService;
import br.com.oficina.infrastructure.security.JwtService;
import br.com.oficina.infrastructure.security.SecurityConfig;
import org.springframework.context.annotation.Import;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

@WebMvcTest(PecaController.class)
@Import(SecurityConfig.class)
@Epic("Gestão de Estoque")
@Feature("API REST Peças")
class PecaControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    @MockBean private PecaService pecaService;
    @MockBean private JwtService jwtService;
    @MockBean private UserDetailsService userDetailsService;

    private final PecaResponse resp = new PecaResponse(1L, "Filtro de Ar", "Motor", 20, new BigDecimal("45"), 5, false);

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Criar peça via API")
    void deveCriar() throws Exception {
        when(pecaService.criar(any())).thenReturn(resp);
        mockMvc.perform(post("/api/pecas").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "nome", "Filtro de Ar", "descricao", "Motor",
                    "quantidadeEstoque", 20, "valorUnitario", 45))))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Buscar peça por ID via API")
    void deveBuscar() throws Exception {
        when(pecaService.buscarPorId(1L)).thenReturn(resp);
        mockMvc.perform(get("/api/pecas/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listar todas as peças via API")
    void deveListar() throws Exception {
        when(pecaService.listarTodas()).thenReturn(List.of(resp));
        mockMvc.perform(get("/api/pecas")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listar peças com estoque baixo via API")
    void estoqueBaixo() throws Exception {
        when(pecaService.listarEstoqueBaixo()).thenReturn(List.of(resp));
        mockMvc.perform(get("/api/pecas/estoque-baixo")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Atualizar peça via API")
    void deveAtualizar() throws Exception {
        when(pecaService.atualizar(eq(1L), any())).thenReturn(resp);
        mockMvc.perform(put("/api/pecas/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "nome", "Filtro Novo", "quantidadeEstoque", 30, "valorUnitario", 50))))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Repor estoque de peça via API")
    void deveReporEstoque() throws Exception {
        when(pecaService.reporEstoque(1L, 10)).thenReturn(resp);
        mockMvc.perform(patch("/api/pecas/1/repor-estoque?quantidade=10").with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Excluir peça via API")
    void deveExcluir() throws Exception {
        mockMvc.perform(delete("/api/pecas/1").with(csrf())).andExpect(status().isNoContent());
    }

    @Test
    @Story("Rejeitar acesso sem autenticação")
    void semAutenticacao_retorna401() throws Exception {
        mockMvc.perform(get("/api/pecas"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 para peça não encontrada")
    void pecaNaoEncontrada_retorna404() throws Exception {
        when(pecaService.buscarPorId(99L))
            .thenThrow(new RecursoNaoEncontradoException("Peça não encontrada: 99"));

        mockMvc.perform(get("/api/pecas/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Peça não encontrada: 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 ao atualizar peça inexistente")
    void pecaNaoEncontradaAoAtualizar_retorna404() throws Exception {
        when(pecaService.atualizar(eq(99L), any()))
            .thenThrow(new RecursoNaoEncontradoException("Peça não encontrada: 99"));

        mockMvc.perform(put("/api/pecas/99").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "nome", "Filtro", "quantidadeEstoque", 5, "valorUnitario", 30))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Peça não encontrada: 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 ao excluir peça inexistente")
    void pecaNaoEncontradaAoExcluir_retorna404() throws Exception {
        doThrow(new RecursoNaoEncontradoException("Peça não encontrada: 99")).when(pecaService).excluir(99L);

        mockMvc.perform(delete("/api/pecas/99").with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Peça não encontrada: 99"));
    }

    @Test
    @WithMockUser(roles = "ATENDENTE")
    @Story("Atendente não pode gerir peças")
    void atendenteNaoPodeGerirPecas_retorna403() throws Exception {
        mockMvc.perform(get("/api/pecas"))
            .andExpect(status().isForbidden());
    }
}

