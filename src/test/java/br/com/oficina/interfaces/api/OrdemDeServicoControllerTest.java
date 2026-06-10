package br.com.oficina.interfaces.api;

import br.com.oficina.application.dto.*;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.service.OrdemDeServicoService;
import br.com.oficina.domain.ordemservico.StatusOS;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

@WebMvcTest(OrdemDeServicoController.class)
@Import(SecurityConfig.class)
@Epic("Ordem de Serviço")
@Feature("API REST Ordens de Serviço")
class OrdemDeServicoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    @MockBean private OrdemDeServicoService osService;
    @MockBean private JwtService jwtService;
    @MockBean private UserDetailsService userDetailsService;

    private OrdemServicoResponse osResp() {
        return new OrdemServicoResponse(
            1L, "OS-2026-00001", "RECEBIDA", LocalDateTime.now(), null,
            "Maria", "529.982.247-25", "ABC1D23", "Honda Civic",
            List.of(), BigDecimal.ZERO, null, null
        );
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Criar OS via API")
    void deveCriarOS() throws Exception {
        when(osService.criarOS(any())).thenReturn(osResp());

        var req = new CriarOrdemServicoRequest(1L, 1L, null);
        mockMvc.perform(post("/api/ordens-servico").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("RECEBIDA"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Buscar OS por ID via API")
    void deveBuscarPorId() throws Exception {
        when(osService.buscarPorId(1L)).thenReturn(osResp());
        mockMvc.perform(get("/api/ordens-servico/1"))
            .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Consultar status da OS via API")
    void deveConsultarStatusPorId() throws Exception {
        when(osService.consultarStatus(1L)).thenReturn("RECEBIDA");
        mockMvc.perform(get("/api/ordens-servico/1/status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("RECEBIDA"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listar OS por status via API")
    void deveListarPorStatus() throws Exception {
        when(osService.listarPorStatus(StatusOS.RECEBIDA)).thenReturn(List.of(osResp()));
        mockMvc.perform(get("/api/ordens-servico/status/RECEBIDA"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listar todas as OS via API")
    void deveListarTodas() throws Exception {
        when(osService.listarTodas()).thenReturn(List.of(osResp()));
        mockMvc.perform(get("/api/ordens-servico"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    // rota interna — requer perfil ATENDENTE ou GESTOR
    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listar OS por cliente via API (uso interno — ATENDENTE/GESTOR)")
    void clienteConsultaOS() throws Exception {
        when(osService.listarPorCliente(1L)).thenReturn(List.of(osResp()));
        mockMvc.perform(get("/api/ordens-servico/cliente/1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Aprovar orçamento via API")
    void clienteAprovaOrcamento() throws Exception {
        when(osService.aprovarOrcamento(1L)).thenReturn(osResp());
        mockMvc.perform(patch("/api/ordens-servico/1/aprovar").with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Rejeitar orçamento via API")
    void clienteRejeitaOrcamento() throws Exception {
        when(osService.rejeitarOrcamento(1L)).thenReturn(osResp());
        mockMvc.perform(patch("/api/ordens-servico/1/rejeitar").with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Iniciar diagnóstico via API")
    void iniciarDiagnostico() throws Exception {
        when(osService.iniciarDiagnostico(1L)).thenReturn(osResp());
        mockMvc.perform(patch("/api/ordens-servico/1/iniciar-diagnostico").with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Transição inválida de diagnóstico retorna 409")
    void iniciarDiagnosticoTransicaoInvalida_retorna409() throws Exception {
        when(osService.iniciarDiagnostico(1L))
            .thenThrow(new IllegalStateException("Transição inválida: RECEBIDA -> EM_DIAGNOSTICO. Status atual: EM_DIAGNOSTICO"));
        mockMvc.perform(patch("/api/ordens-servico/1/iniciar-diagnostico").with(csrf()))
            .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Gerar orçamento via API")
    void gerarOrcamento() throws Exception {
        var orcResp = new OrcamentoResponse(1L, 1L, "ENVIADO", new BigDecimal("500"), LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        when(osService.gerarOrcamento(1L)).thenReturn(orcResp);

        mockMvc.perform(post("/api/ordens-servico/1/orcamento").with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ENVIADO"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Finalizar serviço via API")
    void finalizarServico() throws Exception {
        when(osService.finalizarServico(1L)).thenReturn(osResp());
        mockMvc.perform(patch("/api/ordens-servico/1/finalizar").with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Registrar pagamento via API")
    void registrarPagamento() throws Exception {
        when(osService.registrarPagamento(1L, "PIX")).thenReturn(osResp());
        mockMvc.perform(post("/api/ordens-servico/1/pagamento?metodoPagamento=PIX").with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Consultar tempo médio de execução via API")
    void tempoMedioExecucao() throws Exception {
        when(osService.calcularTempoMedioExecucao()).thenReturn(45.0);
        when(osService.calcularTempoMedioAtendimento()).thenReturn(60.0);
        mockMvc.perform(get("/api/ordens-servico/metricas/tempo-medio"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tempoMedioExecucao").value("45min"))
            .andExpect(jsonPath("$.tempoMedioAtendimento").value("1h"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Erro de negócio retorna 422")
    void erroNegocio_retorna422() throws Exception {
        when(osService.gerarOrcamento(1L)).thenThrow(new NegocioException("OS sem itens"));
        mockMvc.perform(post("/api/ordens-servico/1/orcamento").with(csrf()))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.erro").value("OS sem itens"));
    }

    @Test
    @Story("Sem autenticação retorna 401")
    void semAutenticacao_retorna401() throws Exception {
        mockMvc.perform(get("/api/ordens-servico"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("OS não encontrada retorna 404")
    void osNaoEncontrada_retorna404() throws Exception {
        when(osService.buscarPorId(99L))
            .thenThrow(new RecursoNaoEncontradoException("Ordem de serviço não encontrada: 99"));

        mockMvc.perform(get("/api/ordens-servico/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Ordem de serviço não encontrada: 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Cliente não encontrado ao criar OS retorna 404")
    void clienteNaoEncontradoAoCriarOS_retorna404() throws Exception {
        when(osService.criarOS(any()))
            .thenThrow(new RecursoNaoEncontradoException("Cliente não encontrado"));

        var req = new CriarOrdemServicoRequest(99L, 1L, null);
        mockMvc.perform(post("/api/ordens-servico").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Cliente não encontrado"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Aprovar sem orçamento ativo retorna 422")
    void aprovarOrcamentoSemOrcamentoAtivo_retorna422() throws Exception {
        when(osService.aprovarOrcamento(1L))
            .thenThrow(new NegocioException("Nenhum orçamento ativo encontrado para esta OS"));

        mockMvc.perform(patch("/api/ordens-servico/1/aprovar").with(csrf()))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.erro").value("Nenhum orçamento ativo encontrado para esta OS"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Rejeitar sem orçamento ativo retorna 422")
    void rejeitarOrcamentoSemOrcamentoAtivo_retorna422() throws Exception {
        when(osService.rejeitarOrcamento(1L))
            .thenThrow(new NegocioException("Nenhum orçamento ativo para rejeitar"));

        mockMvc.perform(patch("/api/ordens-servico/1/rejeitar").with(csrf()))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.erro").value("Nenhum orçamento ativo para rejeitar"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Pagamento recusado pelo gateway retorna 422")
    void pagamentoRecusadoPeloGateway_retorna422() throws Exception {
        when(osService.registrarPagamento(1L, "PIX"))
            .thenThrow(new NegocioException("Pagamento recusado pelo gateway: Saldo insuficiente"));

        mockMvc.perform(post("/api/ordens-servico/1/pagamento?metodoPagamento=PIX").with(csrf()))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.erro").value("Pagamento recusado pelo gateway: Saldo insuficiente"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Finalizar serviço não encontrado retorna 404")
    void finalizarServicoNaoEncontrado_retorna404() throws Exception {
        when(osService.finalizarServico(99L))
            .thenThrow(new RecursoNaoEncontradoException("Ordem de serviço não encontrada: 99"));

        mockMvc.perform(patch("/api/ordens-servico/99/finalizar").with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Ordem de serviço não encontrada: 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Aprovar orçamento expirado retorna 409")
    void aprovarOrcamentoExpirado_retorna409() throws Exception {
        when(osService.aprovarOrcamento(1L))
            .thenThrow(new IllegalStateException("Orçamento expirado, não pode ser aprovado"));

        mockMvc.perform(patch("/api/ordens-servico/1/aprovar").with(csrf()))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.erro").value("Orçamento expirado, não pode ser aprovado"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Adicionar itens na OS via API")
    void deveAdicionarItensNaOS() throws Exception {
        when(osService.adicionarItens(eq(1L), any())).thenReturn(osResp());

        var itens = List.of(new ItemOSRequest("SERVICO", 1L, 1));
        mockMvc.perform(post("/api/ordens-servico/1/itens").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(itens)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Entregar veículo via API")
    void deveEntregarVeiculo() throws Exception {
        var entregueResp = new OrdemServicoResponse(
            1L, "OS-2026-00001", "ENTREGUE", LocalDateTime.now(), LocalDateTime.now(),
            "Maria", "529.982.247-25", "ABC1D23", "Honda Civic",
            List.of(), BigDecimal.ZERO, null, null
        );
        when(osService.entregarVeiculo(1L)).thenReturn(entregueResp);

        mockMvc.perform(patch("/api/ordens-servico/1/entregar").with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ENTREGUE"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Argumento inválido retorna 400")
    void illegalArgument_retorna400() throws Exception {
        when(osService.buscarPorId(1L))
            .thenThrow(new IllegalArgumentException("Argumento inválido"));

        mockMvc.perform(get("/api/ordens-servico/1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.erro").value("Argumento inválido"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Erro genérico retorna 500")
    void erroGenerico_retorna500() throws Exception {
        when(osService.listarTodas())
            .thenThrow(new RuntimeException("Erro inesperado"));

        mockMvc.perform(get("/api/ordens-servico"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.erro").value("Erro interno do servidor"));
    }

    // --- testes de controle de acesso por perfil ---

    @Test
    @WithMockUser(roles = "MECANICO")
    @Story("Mecânico não pode abrir OS")
    void mecanicoNaoPodeAbrirOS_retorna403() throws Exception {
        var req = new CriarOrdemServicoRequest(1L, 1L, null);
        mockMvc.perform(post("/api/ordens-servico").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ATENDENTE")
    @Story("Atendente não pode iniciar diagnóstico")
    void atendenteNaoPodeIniciarDiagnostico_retorna403() throws Exception {
        mockMvc.perform(patch("/api/ordens-servico/1/iniciar-diagnostico").with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ATENDENTE")
    @Story("Atendente não pode ver métricas")
    void atendenteNaoPodeVerMetricas_retorna403() throws Exception {
        mockMvc.perform(get("/api/ordens-servico/metricas/tempo-medio"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    @Story("Mecânico pode iniciar diagnóstico")
    void mecanicoPodefazerDiagnostico() throws Exception {
        when(osService.iniciarDiagnostico(1L)).thenReturn(osResp());
        mockMvc.perform(patch("/api/ordens-servico/1/iniciar-diagnostico").with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @Story("Buscar OS por número via API")
    @WithMockUser(roles = "GESTOR")
    void deveBuscarPorNumero() throws Exception {
        when(osService.buscarPorNumero("OS-2026-00001")).thenReturn(osResp());
        mockMvc.perform(get("/api/ordens-servico/numero/OS-2026-00001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.numero").value("OS-2026-00001"));
    }

    @Test
    @Story("Consultar status por número público via API")
    void deveConsultarStatusPorNumeroPublico() throws Exception {
        // rota pública — sem autenticação
        when(osService.buscarPorNumero("OS-2026-00001")).thenReturn(osResp());
        mockMvc.perform(get("/api/ordens-servico/numero/OS-2026-00001/status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.numero").value("OS-2026-00001"))
            .andExpect(jsonPath("$.status").value("RECEBIDA"))
            .andExpect(jsonPath("$.mecanicoNome").isEmpty());
    }

    @Test
    @Story("Consultar status por número não encontrado retorna 404")
    void consultarStatusPorNumeroNaoEncontrado_retorna404() throws Exception {
        when(osService.buscarPorNumero("OS-2026-99999"))
            .thenThrow(new RecursoNaoEncontradoException("Ordem de serviço não encontrada: OS-2026-99999"));
        mockMvc.perform(get("/api/ordens-servico/numero/OS-2026-99999/status"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Ordem de serviço não encontrada: OS-2026-99999"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Métricas de OS específica via API")
    void deveRetornarMetricasOS() throws Exception {
        var metricas = new java.util.LinkedHashMap<String, Object>();
        metricas.put("numero", "OS-2026-00001");
        metricas.put("status", "ENTREGUE");
        metricas.put("dataCriacao", null);
        metricas.put("dataFinalizacao", null);
        metricas.put("tempoExecucao", "1h 30min");
        metricas.put("dataEntrega", null);
        metricas.put("tempoAtendimento", "2h");
        when(osService.calcularMetricasOS(1L)).thenReturn(metricas);

        mockMvc.perform(get("/api/ordens-servico/1/metricas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.numero").value("OS-2026-00001"))
            .andExpect(jsonPath("$.tempoExecucao").value("1h 30min"))
            .andExpect(jsonPath("$.tempoAtendimento").value("2h"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Métricas de OS inexistente retorna 404")
    void metricasOSInexistente_retorna404() throws Exception {
        when(osService.calcularMetricasOS(99L))
            .thenThrow(new RecursoNaoEncontradoException("Ordem de serviço não encontrada: 99"));

        mockMvc.perform(get("/api/ordens-servico/99/metricas"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Ordem de serviço não encontrada: 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Tempo médio retorna 'sem dados' quando não há OS entregues")
    void tempoMedioExecucaoSemDados() throws Exception {
        when(osService.calcularTempoMedioExecucao()).thenReturn(0.0);
        when(osService.calcularTempoMedioAtendimento()).thenReturn(0.0);

        mockMvc.perform(get("/api/ordens-servico/metricas/tempo-medio"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tempoMedioExecucao").value("sem dados"))
            .andExpect(jsonPath("$.tempoMedioAtendimento").value("sem dados"));
    }

    @Test
    @WithMockUser(roles = "ATENDENTE")
    @Story("Atendente não pode ver métricas de OS específica")
    void atendenteNaoPodeVerMetricasOS_retorna403() throws Exception {
        mockMvc.perform(get("/api/ordens-servico/1/metricas"))
            .andExpect(status().isForbidden());
    }

}

