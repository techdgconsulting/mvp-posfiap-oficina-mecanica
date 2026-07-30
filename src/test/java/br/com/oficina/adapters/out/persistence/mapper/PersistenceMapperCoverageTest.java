package br.com.oficina.adapters.out.persistence.mapper;

import br.com.oficina.adapters.out.persistence.jpa.ItemOSJpaEntity;
import br.com.oficina.adapters.out.persistence.jpa.OrdemDeServicoJpaEntity;
import br.com.oficina.domain.model.Diagnostico;
import br.com.oficina.domain.model.ItemOS;
import br.com.oficina.domain.valueobject.CpfCnpj;
import br.com.oficina.domain.valueobject.MetodoPagamento;
import br.com.oficina.domain.valueobject.PeriodoExecucao;
import br.com.oficina.domain.valueobject.Placa;
import br.com.oficina.domain.valueobject.Quantidade;
import br.com.oficina.domain.valueobject.StatusDiagnostico;
import br.com.oficina.domain.valueobject.StatusEncerramento;
import br.com.oficina.domain.valueobject.StatusEntrega;
import br.com.oficina.domain.valueobject.StatusExecucao;
import br.com.oficina.domain.valueobject.StatusOrcamento;
import br.com.oficina.domain.valueobject.StatusOS;
import br.com.oficina.domain.valueobject.StatusPagamento;
import br.com.oficina.domain.valueobject.TipoItem;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PersistenceMapperCoverageTest {

    private final List<Object> mappers = List.of(
            new ClientePersistenceMapper(),
            new EncerramentoPersistenceMapper(),
            new EntregaPersistenceMapper(),
            new ExecucaoPersistenceMapper(),
            new OrcamentoPersistenceMapper(),
            new OrdemDeServicoPersistenceMapper(),
            new PagamentoPersistenceMapper(),
            new PecaPersistenceMapper(),
            new ServicoPersistenceMapper(),
            new UsuarioPersistenceMapper(),
            new VeiculoPersistenceMapper()
    );

    @Test
    void mapearEntidadesJpaEDominioNosDoisSentidos() throws Exception {
        for (var mapper : mappers) {
            for (var method : mapper.getClass().getDeclaredMethods()) {
                if (!Modifier.isPublic(method.getModifiers()) || method.getParameterCount() != 1) {
                    continue;
                }
                var input = sample(method.getParameterTypes()[0]);
                var output = method.invoke(mapper, input);
                assertNotNull(output, mapper.getClass().getSimpleName() + "." + method.getName());
            }
        }
    }

    @Test
    void ordemDeServicoMapperPreservaVinculoDoItemJpaComAOrdem() throws Exception {
        var mapper = new OrdemDeServicoPersistenceMapper();
        var entity = (OrdemDeServicoJpaEntity) sample(OrdemDeServicoJpaEntity.class);
        entity.getItens().clear();
        entity.adicionarItem((ItemOSJpaEntity) sample(ItemOSJpaEntity.class));

        var domain = mapper.toDomain(entity);
        var remapped = mapper.toEntity(domain);

        assertNotNull(domain.getItens().get(0).getOrdemDeServicoId());
        assertNotNull(remapped.getItens().get(0).getOrdemDeServico());
    }

    public static Object sample(Class<?> type) throws Exception {
        if (type.equals(String.class)) {
            return "valor";
        }
        if (type.equals(Long.class) || type.equals(long.class)) {
            return 1L;
        }
        if (type.equals(Integer.class) || type.equals(int.class)) {
            return 2;
        }
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return true;
        }
        if (type.equals(BigDecimal.class)) {
            return new BigDecimal("123.45");
        }
        if (type.equals(LocalDateTime.class)) {
            return LocalDateTime.now();
        }
        if (type.equals(CpfCnpj.class)) {
            return new CpfCnpj("12345678909");
        }
        if (type.equals(Placa.class)) {
            return new Placa("ABC1D23");
        }
        if (type.equals(Quantidade.class)) {
            return new Quantidade(10);
        }
        if (type.equals(PeriodoExecucao.class)) {
            var periodo = new PeriodoExecucao(LocalDateTime.now().minusHours(1));
            periodo.finalizarEm(LocalDateTime.now());
            return periodo;
        }
        if (type.isEnum()) {
            return switch (type.getSimpleName()) {
                case "StatusDiagnostico" -> StatusDiagnostico.CONCLUIDO;
                case "StatusEncerramento" -> StatusEncerramento.ENCERRADA;
                case "StatusEntrega" -> StatusEntrega.VEICULO_ENTREGUE;
                case "StatusExecucao" -> StatusExecucao.SERVICO_FINALIZADO;
                case "StatusOrcamento" -> StatusOrcamento.APROVADO;
                case "StatusOS" -> StatusOS.FINALIZADA;
                case "StatusPagamento" -> StatusPagamento.APROVADO;
                case "TipoItem" -> TipoItem.PECA;
                case "MetodoPagamento" -> MetodoPagamento.PIX;
                default -> type.getEnumConstants()[0];
            };
        }
        if (List.class.isAssignableFrom(type)) {
            return new ArrayList<>();
        }

        try {
            var constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            var instance = constructor.newInstance();
            fillFields(instance);
            return instance;
        } catch (NoSuchMethodException ignored) {
            return instantiateWithBuilder(type);
        }
    }

    private static Object instantiateWithBuilder(Class<?> type) throws Exception {
        var builder = type.getDeclaredMethod("builder").invoke(null);
        for (var method : builder.getClass().getDeclaredMethods()) {
            if (method.getParameterCount() == 1) {
                method.setAccessible(true);
                method.invoke(builder, sample(method.getParameterTypes()[0]));
            }
        }
        var build = builder.getClass().getDeclaredMethod("build");
        build.setAccessible(true);
        return build.invoke(builder);
    }

    private static void fillFields(Object instance) throws Exception {
        for (Class<?> current = instance.getClass(); current != null; current = current.getSuperclass()) {
            for (var field : current.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                field.set(instance, valueForField(instance, field));
            }
        }
    }

    private static Object valueForField(Object owner, Field field) throws Exception {
        var type = field.getType();
        var name = field.getName();

        if (name.equals("documento")) {
            return type.equals(String.class) ? "12345678909" : new CpfCnpj("12345678909");
        }
        if (name.equals("placa")) {
            return type.equals(String.class) ? "ABC1D23" : new Placa("ABC1D23");
        }
        if (name.equals("quantidadeEstoque")) {
            return type.equals(Quantidade.class) ? new Quantidade(10) : 10;
        }
        if (name.equals("tipoDocumento")) {
            return "CPF";
        }
        if (name.equals("diagnostico")) {
            if (type.getPackageName().contains(".persistence.jpa")) {
                return sample(type);
            }
            return Diagnostico.builder()
                    .id(1L)
                    .descricaoProblema("Barulho")
                    .status(StatusDiagnostico.CONCLUIDO)
                    .dataDiagnostico(LocalDateTime.now())
                    .build();
        }
        if (name.equals("periodoExecucao")) {
            return sample(PeriodoExecucao.class);
        }
        if (name.equals("itens")) {
            var itens = new ArrayList<>();
            if (owner instanceof OrdemDeServicoJpaEntity) {
                itens.add(sample(ItemOSJpaEntity.class));
            } else {
                itens.add(ItemOS.builder()
                        .id(1L)
                        .tipo(TipoItem.PECA)
                        .descricao("Filtro")
                        .quantidade(2)
                        .valorUnitario(new BigDecimal("50.00"))
                        .referenciaId(7L)
                        .estoqueReduzido(true)
                        .ordemDeServicoId(1L)
                        .build());
            }
            return itens;
        }
        if (name.equals("ordemDeServico") && owner instanceof ItemOSJpaEntity) {
            return null;
        }
        if (name.equals("perfil")) {
            return type.equals(String.class) ? "ATENDENTE" : sample(type);
        }
        if (name.equals("role")) {
            return "ATENDENTE";
        }
        if (name.equals("metodo")) {
            return MetodoPagamento.PIX;
        }
        if (name.equals("status")) {
            return sample(type);
        }

        return sample(type);
    }

    @SuppressWarnings("unused")
    private static void invokeGetter(Object instance, Method method) throws Exception {
        method.invoke(instance);
    }
}
