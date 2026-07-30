package br.com.oficina.adapters.in.web.controller;

import br.com.oficina.adapters.in.web.mapper.OrdemDeServicoWebMapper;
import br.com.oficina.adapters.in.web.request.CriarOrdemServicoCompletaRequest;
import br.com.oficina.adapters.in.web.request.CriarOrdemServicoRequest;
import br.com.oficina.adapters.in.web.request.ItemOSRequest;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.AdicionarItensOrdemServicoInputPort;
import br.com.oficina.application.port.in.AprovarOrcamentoInputPort;
import br.com.oficina.application.port.in.BuscarOrdemServicoPorIdInputPort;
import br.com.oficina.application.port.in.BuscarOrdemServicoPorNumeroInputPort;
import br.com.oficina.application.port.in.CalcularMetricasOSInputPort;
import br.com.oficina.application.port.in.CalcularTempoMedioOSInputPort;
import br.com.oficina.application.port.in.ConsultarStatusOrdemServicoInputPort;
import br.com.oficina.application.port.in.CriarOrdemServicoCompletaInputPort;
import br.com.oficina.application.port.in.CriarOrdemServicoInputPort;
import br.com.oficina.application.port.in.EntregarVeiculoInputPort;
import br.com.oficina.application.port.in.EnviarNotificacaoOrcamentoInputPort;
import br.com.oficina.application.port.in.FinalizarServicoInputPort;
import br.com.oficina.application.port.in.GerarOrcamentoInputPort;
import br.com.oficina.application.port.in.IniciarDiagnosticoInputPort;
import br.com.oficina.application.port.in.ListarFilaOrdensServicoInputPort;
import br.com.oficina.application.port.in.ListarOrdensServicoInputPort;
import br.com.oficina.application.port.in.ListarOrdensServicoPorClienteInputPort;
import br.com.oficina.application.port.in.ListarOrdensServicoPorStatusInputPort;
import br.com.oficina.application.port.in.RegistrarPagamentoInputPort;
import br.com.oficina.application.port.in.RejeitarOrcamentoInputPort;
import br.com.oficina.application.query.MetricasOSResult;
import br.com.oficina.application.query.NotificacaoOrcamentoResult;
import br.com.oficina.application.query.OrcamentoResult;
import br.com.oficina.application.query.OrdemServicoResult;
import br.com.oficina.application.query.TempoMedioOSResult;
import br.com.oficina.domain.valueobject.StatusOS;
import br.com.oficina.application.port.out.TokenProviderPort;
import br.com.oficina.infrastructure.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdemDeServicoController.class)
@Import({SecurityConfig.class, OrdemDeServicoWebMapper.class})
@Epic("Ordem de Servico")
@Feature("API REST Ordens de Servico")
class OrdemDeServicoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    @MockitoBean private CriarOrdemServicoInputPort criarOrdemServicoInputPort;
    @MockitoBean private CriarOrdemServicoCompletaInputPort criarOrdemServicoCompletaInputPort;
    @MockitoBean private BuscarOrdemServicoPorIdInputPort buscarOrdemServicoPorIdInputPort;
    @MockitoBean private BuscarOrdemServicoPorNumeroInputPort buscarOrdemServicoPorNumeroInputPort;
    @MockitoBean private ListarOrdensServicoInputPort listarOrdensServicoInputPort;
    @MockitoBean private ListarFilaOrdensServicoInputPort listarFilaOrdensServicoInputPort;
    @MockitoBean private ListarOrdensServicoPorClienteInputPort listarOrdensServicoPorClienteInputPort;
    @MockitoBean private ListarOrdensServicoPorStatusInputPort listarOrdensServicoPorStatusInputPort;
    @MockitoBean private ConsultarStatusOrdemServicoInputPort consultarStatusOrdemServicoInputPort;
    @MockitoBean private IniciarDiagnosticoInputPort iniciarDiagnosticoInputPort;
    @MockitoBean private GerarOrcamentoInputPort gerarOrcamentoInputPort;
    @MockitoBean private EnviarNotificacaoOrcamentoInputPort enviarNotificacaoOrcamentoInputPort;
    @MockitoBean private AprovarOrcamentoInputPort aprovarOrcamentoInputPort;
    @MockitoBean private RejeitarOrcamentoInputPort rejeitarOrcamentoInputPort;
    @MockitoBean private FinalizarServicoInputPort finalizarServicoInputPort;
    @MockitoBean private RegistrarPagamentoInputPort registrarPagamentoInputPort;
    @MockitoBean private EntregarVeiculoInputPort entregarVeiculoInputPort;
    @MockitoBean private AdicionarItensOrdemServicoInputPort adicionarItensOrdemServicoInputPort;
    @MockitoBean private CalcularMetricasOSInputPort calcularMetricasOSInputPort;
    @MockitoBean private CalcularTempoMedioOSInputPort calcularTempoMedioOSInputPort;
    @MockitoBean private TokenProviderPort tokenProviderPort;
    @MockitoBean private UserDetailsService userDetailsService;

    private OrdemServicoResult osResp() {
        return new OrdemServicoResult(
            1L, "OS-2026-00001", "RECEBIDA", LocalDateTime.now(), null,
            "Maria", "529.982.247-25", "ABC1D23", "Honda Civic",
            List.of(), BigDecimal.ZERO, null, null
        );
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Criar OS via API")
    void deveCriarOS() throws Exception {
        when(criarOrdemServicoInputPort.execute(any())).thenReturn(osResp());

        var req = new CriarOrdemServicoRequest(1L, 1L, null);
        mockMvc.perform(post("/api/ordens-servico").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("RECEBIDA"));
    }

    @Test
    @WithMockUser(roles = "ATENDENTE")
    @Story("Criar OS completa via API")
    void deveCriarOSCompleta() throws Exception {
        when(criarOrdemServicoCompletaInputPort.execute(any())).thenReturn(osResp());

        var req = new CriarOrdemServicoCompletaRequest(
            new CriarOrdemServicoCompletaRequest.ClienteCompletoRequest(
                "52998224725", "Maria", "11999999999", "maria@email.com",
                "01001000", "Praca da Se", "Se", "Sao Paulo", "SP"),
            new CriarOrdemServicoCompletaRequest.VeiculoCompletoRequest(
                "ABC1D23", "Honda", "Civic", 2020),
            List.of(new CriarOrdemServicoCompletaRequest.ServicoOSRequest(1L, 1)),
            List.of(new CriarOrdemServicoCompletaRequest.PecaOSRequest(2L, 2))
        );

        mockMvc.perform(post("/api/ordens-servico/completa").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.numero").value("OS-2026-00001"))
            .andExpect(jsonPath("$.status").value("RECEBIDA"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Buscar OS por ID via API")
    void deveBuscarPorId() throws Exception {
        when(buscarOrdemServicoPorIdInputPort.execute(1L)).thenReturn(osResp());
        mockMvc.perform(get("/api/ordens-servico/1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Consultar status da OS via API")
    void deveConsultarStatusPorId() throws Exception {
        when(consultarStatusOrdemServicoInputPort.executeStatus(1L)).thenReturn("RECEBIDA");
        mockMvc.perform(get("/api/ordens-servico/1/status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("RECEBIDA"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listar OS por status via API")
    void deveListarPorStatus() throws Exception {
        when(listarOrdensServicoPorStatusInputPort.execute(StatusOS.RECEBIDA)).thenReturn(List.of(osResp()));
        mockMvc.perform(get("/api/ordens-servico/status/RECEBIDA"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listar todas as OS via API")
    void deveListarTodas() throws Exception {
        when(listarOrdensServicoInputPort.execute()).thenReturn(List.of(osResp()));
        mockMvc.perform(get("/api/ordens-servico"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    @Story("Listar fila operacional de OS via API")
    void deveListarFilaOperacional() throws Exception {
        when(listarFilaOrdensServicoInputPort.listarFilaOperacional()).thenReturn(List.of(osResp()));

        mockMvc.perform(get("/api/ordens-servico/fila"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].status").value("RECEBIDA"));

        verify(listarFilaOrdensServicoInputPort).listarFilaOperacional();
    }

    @Test
    @Story("Fila operacional exige autenticacao")
    void listarFilaSemAutenticacao_retorna401() throws Exception {
        mockMvc.perform(get("/api/ordens-servico/fila"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listar OS por cliente via API")
    void clienteConsultaOS() throws Exception {
        when(listarOrdensServicoPorClienteInputPort.executeByCliente(1L)).thenReturn(List.of(osResp()));
        mockMvc.perform(get("/api/ordens-servico/cliente/1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Aprovar orcamento via API")
    void clienteAprovaOrcamento() throws Exception {
        when(aprovarOrcamentoInputPort.execute(any())).thenReturn(osResp());
        mockMvc.perform(patch("/api/ordens-servico/1/aprovar").with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Rejeitar orcamento via API")
    void clienteRejeitaOrcamento() throws Exception {
        when(rejeitarOrcamentoInputPort.execute(any())).thenReturn(osResp());
        mockMvc.perform(patch("/api/ordens-servico/1/rejeitar").with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Iniciar diagnostico via API")
    void iniciarDiagnostico() throws Exception {
        when(iniciarDiagnosticoInputPort.execute(any())).thenReturn(osResp());
        mockMvc.perform(patch("/api/ordens-servico/1/iniciar-diagnostico").with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Transicao invalida de diagnostico retorna 409")
    void iniciarDiagnosticoTransicaoInvalida_retorna409() throws Exception {
        when(iniciarDiagnosticoInputPort.execute(any()))
            .thenThrow(new IllegalStateException("Transicao invalida"));
        mockMvc.perform(patch("/api/ordens-servico/1/iniciar-diagnostico").with(csrf()))
            .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Gerar orcamento via API")
    void gerarOrcamento() throws Exception {
        var orcResp = new OrcamentoResult(1L, 1L, "ENVIADO", new BigDecimal("500"), LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        when(gerarOrcamentoInputPort.execute(any())).thenReturn(orcResp);

        mockMvc.perform(post("/api/ordens-servico/1/orcamento").with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ENVIADO"));
    }

    @Test
    @WithMockUser(roles = "ATENDENTE")
    @Story("Notificar cliente sobre orcamento")
    void deveNotificarClienteSobreOrcamento() throws Exception {
        var result = new NotificacaoOrcamentoResult(
                1L,
                1L,
                "OS-2026-00001",
                "maria@email.com",
                LocalDateTime.now().plusHours(48),
                "http://localhost:8080/api/orcamentos/decisoes-cliente/token/aprovar",
                "http://localhost:8080/api/orcamentos/decisoes-cliente/token/recusar");
        when(enviarNotificacaoOrcamentoInputPort.execute(any())).thenReturn(result);

        mockMvc.perform(post("/api/ordens-servico/1/orcamento/notificar-cliente").with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.emailDestino").value("maria@email.com"))
            .andExpect(jsonPath("$.linkAprovacao").value(result.linkAprovacao()));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Finalizar servico via API")
    void finalizarServico() throws Exception {
        when(finalizarServicoInputPort.execute(any())).thenReturn(osResp());
        mockMvc.perform(patch("/api/ordens-servico/1/finalizar").with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Registrar pagamento via API")
    void registrarPagamento() throws Exception {
        when(registrarPagamentoInputPort.execute(any())).thenReturn(osResp());
        mockMvc.perform(post("/api/ordens-servico/1/pagamento?metodoPagamento=PIX").with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Consultar tempo medio via API")
    void tempoMedioExecucao() throws Exception {
        when(calcularTempoMedioOSInputPort.executeTempoMedio()).thenReturn(new TempoMedioOSResult(45.0, 60.0));
        mockMvc.perform(get("/api/ordens-servico/metricas/tempo-medio"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tempoMedioExecucao").value("45min"))
            .andExpect(jsonPath("$.tempoMedioAtendimento").value("1h"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Erro de negocio retorna 422")
    void erroNegocio_retorna422() throws Exception {
        when(gerarOrcamentoInputPort.execute(any())).thenThrow(new NegocioException("OS sem itens"));
        mockMvc.perform(post("/api/ordens-servico/1/orcamento").with(csrf()))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.erro").value("OS sem itens"));
    }

    @Test
    @Story("Sem autenticacao retorna 401")
    void semAutenticacao_retorna401() throws Exception {
        mockMvc.perform(get("/api/ordens-servico"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("OS nao encontrada retorna 404")
    void osNaoEncontrada_retorna404() throws Exception {
        when(buscarOrdemServicoPorIdInputPort.execute(99L))
            .thenThrow(new RecursoNaoEncontradoException("Ordem de servico nao encontrada: 99"));

        mockMvc.perform(get("/api/ordens-servico/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Ordem de servico nao encontrada: 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Adicionar itens na OS via API")
    void deveAdicionarItensNaOS() throws Exception {
        when(adicionarItensOrdemServicoInputPort.execute(any())).thenReturn(osResp());

        var itens = List.of(new ItemOSRequest("SERVICO", 1L, 1));
        mockMvc.perform(post("/api/ordens-servico/1/itens").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(itens)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Entregar veiculo via API")
    void deveEntregarVeiculo() throws Exception {
        var entregueResp = new OrdemServicoResult(
            1L, "OS-2026-00001", "ENTREGUE", LocalDateTime.now(), LocalDateTime.now(),
            "Maria", "529.982.247-25", "ABC1D23", "Honda Civic",
            List.of(), BigDecimal.ZERO, null, null
        );
        when(entregarVeiculoInputPort.execute(any())).thenReturn(entregueResp);

        mockMvc.perform(patch("/api/ordens-servico/1/entregar").with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ENTREGUE"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Argumento invalido retorna 400")
    void illegalArgument_retorna400() throws Exception {
        when(buscarOrdemServicoPorIdInputPort.execute(1L))
            .thenThrow(new IllegalArgumentException("Argumento invalido"));

        mockMvc.perform(get("/api/ordens-servico/1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.erro").value("Argumento invalido"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Erro generico retorna 500")
    void erroGenerico_retorna500() throws Exception {
        when(listarOrdensServicoInputPort.execute())
            .thenThrow(new RuntimeException("Erro inesperado"));

        mockMvc.perform(get("/api/ordens-servico"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.erro").value("Erro interno do servidor"));
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    @Story("Mecanico nao pode abrir OS")
    void mecanicoNaoPodeAbrirOS_retorna403() throws Exception {
        var req = new CriarOrdemServicoRequest(1L, 1L, null);
        mockMvc.perform(post("/api/ordens-servico").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ATENDENTE")
    @Story("Atendente nao pode iniciar diagnostico")
    void atendenteNaoPodeIniciarDiagnostico_retorna403() throws Exception {
        mockMvc.perform(patch("/api/ordens-servico/1/iniciar-diagnostico").with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MECANICO")
    @Story("Mecanico pode iniciar diagnostico")
    void mecanicoPodefazerDiagnostico() throws Exception {
        when(iniciarDiagnosticoInputPort.execute(any())).thenReturn(osResp());
        mockMvc.perform(patch("/api/ordens-servico/1/iniciar-diagnostico").with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @Story("Buscar OS por numero via API")
    @WithMockUser(roles = "GESTOR")
    void deveBuscarPorNumero() throws Exception {
        when(buscarOrdemServicoPorNumeroInputPort.execute("OS-2026-00001")).thenReturn(osResp());
        mockMvc.perform(get("/api/ordens-servico/numero/OS-2026-00001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.numero").value("OS-2026-00001"));
    }

    @Test
    @Story("Consultar status por numero publico via API")
    void deveConsultarStatusPorNumeroPublico() throws Exception {
        when(buscarOrdemServicoPorNumeroInputPort.execute("OS-2026-00001")).thenReturn(osResp());
        mockMvc.perform(get("/api/ordens-servico/numero/OS-2026-00001/status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.numero").value("OS-2026-00001"))
            .andExpect(jsonPath("$.status").value("RECEBIDA"))
            .andExpect(jsonPath("$.mecanicoNome").isEmpty());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Metricas de OS especifica via API")
    void deveRetornarMetricasOS() throws Exception {
        var metricas = new MetricasOSResult(
                "OS-2026-00001", "ENTREGUE", null, null, "1h 30min", null, "2h");
        when(calcularMetricasOSInputPort.executeMetricas(1L)).thenReturn(metricas);

        mockMvc.perform(get("/api/ordens-servico/1/metricas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.numero").value("OS-2026-00001"))
            .andExpect(jsonPath("$.tempoExecucao").value("1h 30min"))
            .andExpect(jsonPath("$.tempoAtendimento").value("2h"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Tempo medio sem dados")
    void tempoMedioExecucaoSemDados() throws Exception {
        when(calcularTempoMedioOSInputPort.executeTempoMedio()).thenReturn(new TempoMedioOSResult(0.0, 0.0));

        mockMvc.perform(get("/api/ordens-servico/metricas/tempo-medio"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tempoMedioExecucao").value("sem dados"))
            .andExpect(jsonPath("$.tempoMedioAtendimento").value("sem dados"));
    }
}
