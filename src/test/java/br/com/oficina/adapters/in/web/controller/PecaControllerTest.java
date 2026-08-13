package br.com.oficina.adapters.in.web.controller;

import br.com.oficina.adapters.in.web.mapper.PecaWebMapper;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.AtualizarPecaInputPort;
import br.com.oficina.application.port.in.BaixarEstoqueInputPort;
import br.com.oficina.application.port.in.BuscarPecaPorIdInputPort;
import br.com.oficina.application.port.in.CriarPecaInputPort;
import br.com.oficina.application.port.in.ExcluirPecaInputPort;
import br.com.oficina.application.port.in.ListarPecasComEstoqueBaixoInputPort;
import br.com.oficina.application.port.in.ListarPecasInputPort;
import br.com.oficina.application.port.in.ReporEstoqueInputPort;
import br.com.oficina.application.port.in.VerificarDisponibilidadePecaInputPort;
import br.com.oficina.application.query.PecaResult;
import br.com.oficina.application.port.out.TokenProviderPort;
import br.com.oficina.infrastructure.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PecaController.class)
@Import({SecurityConfig.class, PecaWebMapper.class})
@Epic("Gestao de Estoque")
@Feature("API REST Pecas")
class PecaControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    @MockitoBean private CriarPecaInputPort criarPecaInputPort;
    @MockitoBean private BuscarPecaPorIdInputPort buscarPecaPorIdInputPort;
    @MockitoBean private ListarPecasInputPort listarPecasInputPort;
    @MockitoBean private ListarPecasComEstoqueBaixoInputPort listarPecasComEstoqueBaixoInputPort;
    @MockitoBean private AtualizarPecaInputPort atualizarPecaInputPort;
    @MockitoBean private ReporEstoqueInputPort reporEstoqueInputPort;
    @MockitoBean private BaixarEstoqueInputPort baixarEstoqueInputPort;
    @MockitoBean private VerificarDisponibilidadePecaInputPort verificarDisponibilidadePecaInputPort;
    @MockitoBean private ExcluirPecaInputPort excluirPecaInputPort;
    @MockitoBean private TokenProviderPort tokenProviderPort;
    @MockitoBean private UserDetailsService userDetailsService;

    private final PecaResult resp = new PecaResult(1L, "Filtro de Ar", "Motor", 20, new BigDecimal("45"), 5, false);

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Criar peca via API")
    void deveCriar() throws Exception {
        when(criarPecaInputPort.execute(any())).thenReturn(resp);
        mockMvc.perform(post("/api/pecas").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "nome", "Filtro de Ar", "descricao", "Motor",
                    "quantidadeEstoque", 20, "valorUnitario", 45))))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Buscar peca por ID via API")
    void deveBuscar() throws Exception {
        when(buscarPecaPorIdInputPort.execute(1L)).thenReturn(resp);
        mockMvc.perform(get("/api/pecas/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listar todas as pecas via API")
    void deveListar() throws Exception {
        when(listarPecasInputPort.execute()).thenReturn(List.of(resp));
        mockMvc.perform(get("/api/pecas")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listagem de pecas nao reflete payload suspeito")
    void deveSanitizarPayloadSuspeitoNaListagem() throws Exception {
        var suspeita = new PecaResult(
            2L,
            "John Doe <!--SELECT-->",
            "${__import__('subprocess')}",
            10,
            new BigDecimal("1.20"),
            10,
            true
        );
        when(listarPecasInputPort.execute()).thenReturn(List.of(suspeita));

        mockMvc.perform(get("/api/pecas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nome").value("[conteudo removido]"))
            .andExpect(jsonPath("$[0].descricao").value("[conteudo removido]"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listar pecas com estoque baixo via API")
    void estoqueBaixo() throws Exception {
        when(listarPecasComEstoqueBaixoInputPort.executeEstoqueBaixo()).thenReturn(List.of(resp));
        mockMvc.perform(get("/api/pecas/estoque-baixo")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listagem de estoque baixo nao reflete payload suspeito")
    void deveSanitizarPayloadSuspeitoNoEstoqueBaixo() throws Exception {
        var suspeita = new PecaResult(
            3L,
            "<xsl:value-of select=\"system-property('xsl:vendor')\"/>",
            "Descricao normal",
            1,
            new BigDecimal("1.20"),
            10,
            true
        );
        when(listarPecasComEstoqueBaixoInputPort.executeEstoqueBaixo()).thenReturn(List.of(suspeita));

        mockMvc.perform(get("/api/pecas/estoque-baixo"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nome").value("[conteudo removido]"))
            .andExpect(jsonPath("$[0].descricao").value("Descricao normal"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Rejeitar peca com payload suspeito")
    void deveRejeitarPayloadSuspeitoAoCriar() throws Exception {
        mockMvc.perform(post("/api/pecas").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "nome", "Filtro <!--SELECT-->",
                    "descricao", "Motor",
                    "quantidadeEstoque", 20,
                    "valorUnitario", 45))))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Atualizar peca via API")
    void deveAtualizar() throws Exception {
        when(atualizarPecaInputPort.execute(any())).thenReturn(resp);
        mockMvc.perform(put("/api/pecas/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "nome", "Filtro Novo", "quantidadeEstoque", 30, "valorUnitario", 50))))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Repor estoque de peca via API")
    void deveReporEstoque() throws Exception {
        when(reporEstoqueInputPort.execute(any())).thenReturn(resp);
        mockMvc.perform(patch("/api/pecas/1/repor-estoque?quantidade=10").with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Baixar estoque de peca via API")
    void deveBaixarEstoque() throws Exception {
        when(baixarEstoqueInputPort.execute(any())).thenReturn(resp);
        mockMvc.perform(patch("/api/pecas/1/baixar-estoque?quantidade=5").with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Excluir peca via API")
    void deveExcluir() throws Exception {
        mockMvc.perform(delete("/api/pecas/1").with(csrf())).andExpect(status().isNoContent());
    }

    @Test
    @Story("Rejeitar acesso sem autenticacao")
    void semAutenticacao_retorna401() throws Exception {
        mockMvc.perform(get("/api/pecas"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.erro").value("Nao autorizado"))
            .andExpect(jsonPath("$.path").doesNotExist())
            .andExpect(jsonPath("$.error").doesNotExist())
            .andExpect(jsonPath("$.exception").doesNotExist())
            .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 para peca nao encontrada")
    void pecaNaoEncontrada_retorna404() throws Exception {
        when(buscarPecaPorIdInputPort.execute(99L))
            .thenThrow(new RecursoNaoEncontradoException("Peca nao encontrada: 99"));

        mockMvc.perform(get("/api/pecas/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Peca nao encontrada: 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 ao atualizar peca inexistente")
    void pecaNaoEncontradaAoAtualizar_retorna404() throws Exception {
        when(atualizarPecaInputPort.execute(any()))
            .thenThrow(new RecursoNaoEncontradoException("Peca nao encontrada: 99"));

        mockMvc.perform(put("/api/pecas/99").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "nome", "Filtro", "quantidadeEstoque", 5, "valorUnitario", 30))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Peca nao encontrada: 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 ao excluir peca inexistente")
    void pecaNaoEncontradaAoExcluir_retorna404() throws Exception {
        doThrow(new RecursoNaoEncontradoException("Peca nao encontrada: 99"))
                .when(excluirPecaInputPort).execute(any());

        mockMvc.perform(delete("/api/pecas/99").with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Peca nao encontrada: 99"));
    }

    @Test
    @WithMockUser(roles = "ATENDENTE")
    @Story("Atendente nao pode gerir pecas")
    void atendenteNaoPodeGerirPecas_retorna403() throws Exception {
        mockMvc.perform(get("/api/pecas"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403))
            .andExpect(jsonPath("$.erro").value("Acesso negado"))
            .andExpect(jsonPath("$.path").doesNotExist())
            .andExpect(jsonPath("$.error").doesNotExist())
            .andExpect(jsonPath("$.exception").doesNotExist())
            .andExpect(jsonPath("$.trace").doesNotExist());
    }
}
