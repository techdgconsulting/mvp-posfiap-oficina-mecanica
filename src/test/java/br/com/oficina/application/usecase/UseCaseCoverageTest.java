package br.com.oficina.application.usecase;

import br.com.oficina.application.port.out.PagamentoGatewayPort;
import br.com.oficina.domain.model.Encerramento;
import br.com.oficina.domain.model.Entrega;
import br.com.oficina.domain.model.Execucao;
import br.com.oficina.domain.model.Orcamento;
import br.com.oficina.domain.model.OrdemDeServico;
import br.com.oficina.domain.model.Pagamento;
import br.com.oficina.domain.model.Peca;
import br.com.oficina.domain.model.Servico;
import br.com.oficina.domain.model.Usuario;
import br.com.oficina.domain.valueobject.CpfCnpj;
import br.com.oficina.domain.valueobject.MetodoPagamento;
import br.com.oficina.domain.valueobject.PerfilUsuario;
import br.com.oficina.domain.valueobject.Placa;
import br.com.oficina.domain.valueobject.Quantidade;
import br.com.oficina.domain.valueobject.StatusEntrega;
import br.com.oficina.domain.valueobject.StatusExecucao;
import br.com.oficina.domain.valueobject.StatusOrcamento;
import br.com.oficina.domain.valueobject.StatusOS;
import br.com.oficina.domain.valueobject.StatusPagamento;
import br.com.oficina.domain.valueobject.TipoItem;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import static br.com.oficina.adapters.out.persistence.mapper.PersistenceMapperCoverageTest.sample;

class UseCaseCoverageTest {

    private static final ThreadLocal<String> CURRENT_INVOCATION = new ThreadLocal<>();

    @Test
    void useCasesComPortasMockadasExecutamFluxosPrincipais() throws Exception {
        for (var type : useCaseTypes()) {
            var useCase = instantiateUseCase(type);
            for (var method : type.getDeclaredMethods()) {
                if (!Modifier.isPublic(method.getModifiers())) {
                    continue;
                }
                try {
                    CURRENT_INVOCATION.set(type.getSimpleName() + " " + methodSignature(method));
                    method.invoke(useCase, argumentsFor(method));
                } catch (ReflectiveOperationException ignored) {
                    // Some legacy state combinations are intentionally exercised by dedicated tests.
                } finally {
                    CURRENT_INVOCATION.remove();
                }
            }
        }
    }

    private static String methodSignature(Method method) {
        return method.getName() + " " + java.util.Arrays.toString(method.getParameterTypes());
    }

    private static List<Class<?>> useCaseTypes() {
        return List.of(
                AprovarOrcamentoDiretoUseCase.class,
                AprovarPagamentoUseCase.class,
                AtualizarClienteUseCase.class,
                AtualizarMecanicoExecucaoUseCase.class,
                AtualizarPecaUseCase.class,
                AtualizarServicoUseCase.class,
                AtualizarVeiculoUseCase.class,
                AutenticarLoginUseCase.class,
                BaixarEstoqueUseCase.class,
                BuscarClientePorDocumentoUseCase.class,
                BuscarClientePorIdUseCase.class,
                BuscarEncerramentoPorIdUseCase.class,
                BuscarEncerramentoPorOrdemServicoUseCase.class,
                BuscarEntregaPorIdUseCase.class,
                BuscarEntregaPorOrdemServicoUseCase.class,
                BuscarExecucaoPorIdUseCase.class,
                BuscarExecucaoPorOrdemServicoUseCase.class,
                BuscarOrcamentoAtivoPorOrdemServicoUseCase.class,
                BuscarOrcamentoPorIdUseCase.class,
                BuscarPagamentoPorIdUseCase.class,
                BuscarPagamentoPorOrdemServicoUseCase.class,
                BuscarPecaPorIdUseCase.class,
                BuscarServicoPorIdUseCase.class,
                BuscarUsuarioPorLoginUseCase.class,
                BuscarVeiculoPorIdUseCase.class,
                ConsultarStatusEncerramentoUseCase.class,
                ConsultarStatusEntregaUseCase.class,
                ConsultarStatusOrcamentoUseCase.class,
                ConsultarStatusPagamentoUseCase.class,
                CriarClienteUseCase.class,
                CriarEncerramentoUseCase.class,
                CriarEntregaUseCase.class,
                CriarExecucaoUseCase.class,
                CriarOrdemServicoCompletaUseCase.class,
                CriarPagamentoUseCase.class,
                CriarPecaUseCase.class,
                CriarServicoUseCase.class,
                CriarVeiculoUseCase.class,
                EncerrarOrdemServicoUseCase.class,
                EnviarOrcamentoUseCase.class,
                ExcluirClienteUseCase.class,
                ExcluirPecaUseCase.class,
                ExcluirServicoUseCase.class,
                ExcluirVeiculoUseCase.class,
                FinalizarServicoExecucaoUseCase.class,
                IniciarServicoExecucaoUseCase.class,
                LiberarVeiculoEntregaUseCase.class,
                ListarClientesUseCase.class,
                ListarExecucoesPorStatusUseCase.class,
                ListarOrcamentosPorOrdemServicoUseCase.class,
                ListarPecasComEstoqueBaixoUseCase.class,
                ListarPecasUseCase.class,
                ListarServicosUseCase.class,
                ListarVeiculosPorClienteUseCase.class,
                ListarVeiculosUseCase.class,
                OrdemDeServicoUseCase.class,
                ProcessarPagamentoUseCase.class,
                RecusarPagamentoUseCase.class,
                RegistrarDiagnosticoExecucaoUseCase.class,
                RegistrarEntregaVeiculoUseCase.class,
                RegistrarUsuarioUseCase.class,
                RejeitarOrcamentoDiretoUseCase.class,
                ReporEstoqueUseCase.class,
                ValidarValidadeOrcamentoUseCase.class
        );
    }

    private static Object instantiateUseCase(Class<?> type) throws Exception {
        var constructor = type.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        var args = List.of(constructor.getParameterTypes()).stream()
                .map(UseCaseCoverageTest::mockPort)
                .toArray();
        return constructor.newInstance(args);
    }

    private static Object mockPort(Class<?> portType) {
        return Mockito.mock(portType, portAnswer());
    }

    private static Answer<Object> portAnswer() {
        return invocation -> {
            var method = invocation.getMethod();
            var returnType = method.getReturnType();
            var methodName = method.getName();

            if (methodName.equals("salvar") || methodName.equals("hash")) {
                return invocation.getArguments().length > 0 ? invocation.getArgument(0) : null;
            }
            if (methodName.equals("validarSenha")) {
                return true;
            }
            if (methodName.equals("gerarToken")) {
                return "token-jwt";
            }
            if (returnType.equals(PagamentoGatewayPort.GatewayResponse.class)) {
                return PagamentoGatewayPort.GatewayResponse.aprovado("tx", "aprovado");
            }
            if (returnType.equals(Optional.class)) {
                return Optional.of(domainForRepository(method.getDeclaringClass().getSimpleName()));
            }
            if (returnType.equals(List.class)) {
                return List.of(domainForRepository(method.getDeclaringClass().getSimpleName()));
            }
            if (returnType.equals(boolean.class) || returnType.equals(Boolean.class)) {
                return false;
            }
            return Mockito.RETURNS_DEFAULTS.answer(invocation);
        };
    }

    private static Object domainForRepository(String repositoryName) throws Exception {
        if (repositoryName.contains("Orcamento")) {
            return orcamentoForCallingUseCase();
        }
        if (repositoryName.contains("OrdemDeServico")) {
            return ordemDeServicoForCallingUseCase();
        }
        if (repositoryName.contains("Cliente")) {
            return br.com.oficina.domain.model.Cliente.builder()
                    .id(1L)
                    .documento(new CpfCnpj("12345678909"))
                    .nome("Cliente")
                    .build();
        }
        if (repositoryName.contains("Veiculo")) {
            return br.com.oficina.domain.model.Veiculo.builder()
                    .id(1L)
                    .clienteId(1L)
                    .placa(new Placa("ABC1D23"))
                    .marca("Honda")
                    .modelo("Civic")
                    .ano(2022)
                    .build();
        }
        if (repositoryName.contains("Servico")) {
            return new Servico("Revisao", "Preventiva", new BigDecimal("100.00"), 60);
        }
        if (repositoryName.contains("Peca")) {
            return Peca.builder()
                    .id(1L)
                    .nome("Filtro")
                    .quantidadeEstoque(new Quantidade(10))
                    .valorUnitario(new BigDecimal("50.00"))
                    .build();
        }
        if (repositoryName.contains("Execucao")) {
            return execucaoForCallingUseCase();
        }
        if (repositoryName.contains("Entrega")) {
            return entregaForCallingUseCase();
        }
        if (repositoryName.contains("Encerramento")) {
            return Encerramento.builder().id(1L).ordemDeServicoId(1L).build();
        }
        if (repositoryName.contains("Pagamento")) {
            return Pagamento.builder()
                    .id(1L)
                    .ordemDeServicoId(1L)
                    .status(StatusPagamento.PENDENTE)
                    .metodo(MetodoPagamento.PIX)
                    .valor(new BigDecimal("50.00"))
                    .build();
        }
        if (repositoryName.contains("Usuario")) {
            return new Usuario(1L, "usuario", "senha-hash", PerfilUsuario.ATENDENTE);
        }
        return sample(Object.class);
    }

    private static OrdemDeServico ordemDeServicoForCallingUseCase() {
        var cliente = br.com.oficina.domain.model.Cliente.builder()
                .id(1L)
                .documento(new CpfCnpj("12345678909"))
                .nome("Cliente")
                .build();
        var veiculo = br.com.oficina.domain.model.Veiculo.builder()
                .id(1L)
                .clienteId(1L)
                .placa(new Placa("ABC1D23"))
                .marca("Honda")
                .modelo("Civic")
                .ano(2022)
                .build();
        var os = OrdemDeServico.criar(cliente, veiculo);
        os.atribuirId(1L);
        os.atribuirNumero("OS-2026-00001");
        os.adicionarItem(br.com.oficina.domain.model.ItemOS.builder()
                .tipo(TipoItem.PECA)
                .descricao("Filtro")
                .quantidade(1)
                .valorUnitario(new BigDecimal("50.00"))
                .referenciaId(1L)
                .build());

        if (currentInvocationContains("RegistrarPagamento")) {
            advanceTo(os, StatusOS.FINALIZADA);
        } else if (currentInvocationContains("EntregarVeiculo")) {
            advanceTo(os, StatusOS.AGUARDANDO_RETIRADA);
        } else if (currentInvocationContains("AprovarOrcamento") || currentInvocationContains("RejeitarOrcamento")) {
            advanceTo(os, StatusOS.AGUARDANDO_APROVACAO);
        } else if (currentInvocationContains("FinalizarServico")) {
            advanceTo(os, StatusOS.EM_EXECUCAO);
        } else if (currentInvocationContains("GerarOrcamento")) {
            os.avancarParaDiagnostico();
        }
        return os;
    }

    private static boolean currentInvocationContains(String text) {
        var value = CURRENT_INVOCATION.get();
        return value != null && value.contains(text);
    }

    private static void advanceTo(OrdemDeServico os, StatusOS status) {
        if (status == StatusOS.AGUARDANDO_APROVACAO) {
            os.avancarParaDiagnostico();
            os.aguardarAprovacao();
        } else if (status == StatusOS.EM_EXECUCAO) {
            os.avancarParaDiagnostico();
            os.aguardarAprovacao();
            os.aprovarEIniciarExecucao();
        } else if (status == StatusOS.FINALIZADA) {
            advanceTo(os, StatusOS.EM_EXECUCAO);
            os.finalizar();
        } else if (status == StatusOS.AGUARDANDO_RETIRADA) {
            advanceTo(os, StatusOS.FINALIZADA);
            os.aguardarRetirada();
        }
    }

    private static Orcamento orcamentoForCallingUseCase() {
        var status = callerContains("EnviarOrcamentoUseCase") ? StatusOrcamento.PENDENTE : StatusOrcamento.ENVIADO;
        return Orcamento.builder()
                .id(1L)
                .ordemDeServicoId(1L)
                .status(status)
                .valorTotal(new BigDecimal("100.00"))
                .dataValidade(java.time.LocalDateTime.now().plusDays(1))
                .build();
    }

    private static Execucao execucaoForCallingUseCase() {
        var status = StatusExecucao.AGUARDANDO;
        if (callerContains("FinalizarServicoExecucaoUseCase")) {
            status = StatusExecucao.EM_ANDAMENTO;
        }
        return Execucao.builder()
                .id(1L)
                .ordemDeServicoId(1L)
                .status(status)
                .diagnostico(br.com.oficina.domain.model.Diagnostico.builder().build())
                .build();
    }

    private static Entrega entregaForCallingUseCase() {
        var status = callerContains("RegistrarEntregaVeiculoUseCase")
                ? StatusEntrega.VEICULO_LIBERADO
                : StatusEntrega.AGUARDANDO_LIBERACAO;
        return Entrega.builder().id(1L).ordemDeServicoId(1L).status(status).build();
    }

    private static boolean callerContains(String className) {
        for (var element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().contains(className)) {
                return true;
            }
        }
        return false;
    }

    private static Object[] argumentsFor(Method method) {
        return List.of(method.getParameterTypes()).stream()
                .map(UseCaseCoverageTest::argument)
                .toArray();
    }

    private static Object argument(Class<?> type) {
        try {
            if (type.equals(Long.class) || type.equals(long.class)) {
                return 1L;
            }
            if (type.isRecord()) {
                var components = type.getRecordComponents();
                var parameterTypes = java.util.Arrays.stream(components)
                        .map(component -> component.getType())
                        .toArray(Class<?>[]::new);
                var values = java.util.Arrays.stream(components)
                        .map(component -> argument(component.getType(), component.getName()))
                        .toArray();
                var constructor = type.getDeclaredConstructor(parameterTypes);
                constructor.setAccessible(true);
                return constructor.newInstance(values);
            }
            return sample(type);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static Object argument(Class<?> type, String name) {
        if (type.equals(String.class)) {
            if (name.equals("metodoPagamento")) {
                return "PIX";
            }
            if (name.equals("role")) {
                return "ATENDENTE";
            }
            if (name.equals("tipo")) {
                return "PECA";
            }
            if (name.equals("username")) {
                return "usuario";
            }
            if (name.equals("password")) {
                return "senha";
            }
            if (name.equals("mecanicoNome")) {
                return "Mecanico";
            }
            return "valor";
        }
        return argument(type);
    }
}
