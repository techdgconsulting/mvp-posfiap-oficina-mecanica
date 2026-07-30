package br.com.oficina.adapters.out.persistence;

import br.com.oficina.adapters.out.persistence.jpa.ClienteJpaEntity;
import br.com.oficina.adapters.out.persistence.jpa.EncerramentoJpaEntity;
import br.com.oficina.adapters.out.persistence.jpa.EntregaJpaEntity;
import br.com.oficina.adapters.out.persistence.jpa.ExecucaoJpaEntity;
import br.com.oficina.adapters.out.persistence.jpa.OrcamentoJpaEntity;
import br.com.oficina.adapters.out.persistence.jpa.OrdemDeServicoJpaEntity;
import br.com.oficina.adapters.out.persistence.jpa.PagamentoJpaEntity;
import br.com.oficina.adapters.out.persistence.jpa.PecaJpaEntity;
import br.com.oficina.adapters.out.persistence.jpa.ServicoJpaEntity;
import br.com.oficina.adapters.out.persistence.jpa.UsuarioJpaEntity;
import br.com.oficina.adapters.out.persistence.jpa.VeiculoJpaEntity;
import br.com.oficina.adapters.out.persistence.mapper.ClientePersistenceMapper;
import br.com.oficina.adapters.out.persistence.mapper.EncerramentoPersistenceMapper;
import br.com.oficina.adapters.out.persistence.mapper.EntregaPersistenceMapper;
import br.com.oficina.adapters.out.persistence.mapper.ExecucaoPersistenceMapper;
import br.com.oficina.adapters.out.persistence.mapper.OrcamentoPersistenceMapper;
import br.com.oficina.adapters.out.persistence.mapper.OrdemDeServicoPersistenceMapper;
import br.com.oficina.adapters.out.persistence.mapper.PagamentoPersistenceMapper;
import br.com.oficina.adapters.out.persistence.mapper.PecaPersistenceMapper;
import br.com.oficina.adapters.out.persistence.mapper.PersistenceMapperCoverageTest;
import br.com.oficina.adapters.out.persistence.mapper.ServicoPersistenceMapper;
import br.com.oficina.adapters.out.persistence.mapper.UsuarioPersistenceMapper;
import br.com.oficina.adapters.out.persistence.mapper.VeiculoPersistenceMapper;
import br.com.oficina.adapters.out.persistence.repository.SpringDataClienteRepository;
import br.com.oficina.adapters.out.persistence.repository.SpringDataEncerramentoRepository;
import br.com.oficina.adapters.out.persistence.repository.SpringDataEntregaRepository;
import br.com.oficina.adapters.out.persistence.repository.SpringDataExecucaoRepository;
import br.com.oficina.adapters.out.persistence.repository.SpringDataOrcamentoRepository;
import br.com.oficina.adapters.out.persistence.repository.SpringDataOrdemDeServicoRepository;
import br.com.oficina.adapters.out.persistence.repository.SpringDataPagamentoRepository;
import br.com.oficina.adapters.out.persistence.repository.SpringDataPecaRepository;
import br.com.oficina.adapters.out.persistence.repository.SpringDataServicoRepository;
import br.com.oficina.adapters.out.persistence.repository.SpringDataUsuarioRepository;
import br.com.oficina.adapters.out.persistence.repository.SpringDataVeiculoRepository;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PersistenceAdapterCoverageTest {

    @Test
    void adaptersDelegamParaRepositoriosEMapeiamResultados() {
        var adapters = List.of(
                new ClientePersistenceAdapter(mockRepository(SpringDataClienteRepository.class, ClienteJpaEntity.class),
                        new ClientePersistenceMapper()),
                new EncerramentoPersistenceAdapter(mockRepository(SpringDataEncerramentoRepository.class, EncerramentoJpaEntity.class),
                        new EncerramentoPersistenceMapper()),
                new EntregaPersistenceAdapter(mockRepository(SpringDataEntregaRepository.class, EntregaJpaEntity.class),
                        new EntregaPersistenceMapper()),
                new ExecucaoPersistenceAdapter(mockRepository(SpringDataExecucaoRepository.class, ExecucaoJpaEntity.class),
                        new ExecucaoPersistenceMapper()),
                new OrcamentoPersistenceAdapter(mockRepository(SpringDataOrcamentoRepository.class, OrcamentoJpaEntity.class),
                        new OrcamentoPersistenceMapper()),
                new OrdemDeServicoPersistenceAdapter(mockRepository(SpringDataOrdemDeServicoRepository.class,
                        OrdemDeServicoJpaEntity.class), new OrdemDeServicoPersistenceMapper()),
                new PagamentoPersistenceAdapter(mockRepository(SpringDataPagamentoRepository.class, PagamentoJpaEntity.class),
                        new PagamentoPersistenceMapper()),
                new PecaPersistenceAdapter(mockRepository(SpringDataPecaRepository.class, PecaJpaEntity.class),
                        new PecaPersistenceMapper()),
                new ServicoPersistenceAdapter(mockRepository(SpringDataServicoRepository.class, ServicoJpaEntity.class),
                        new ServicoPersistenceMapper()),
                new UsuarioPersistenceAdapter(mockRepository(SpringDataUsuarioRepository.class, UsuarioJpaEntity.class),
                        new UsuarioPersistenceMapper()),
                new VeiculoPersistenceAdapter(mockRepository(SpringDataVeiculoRepository.class, VeiculoJpaEntity.class),
                        new VeiculoPersistenceMapper())
        );

        for (var adapter : adapters) {
            for (var method : adapter.getClass().getDeclaredMethods()) {
                if (!Modifier.isPublic(method.getModifiers())) {
                    continue;
                }
                assertDoesNotThrow(() -> method.invoke(adapter, argumentsFor(method)),
                        adapter.getClass().getSimpleName() + "." + method.getName());
            }
        }
    }

    private static Object[] argumentsFor(Method method) {
        return List.of(method.getParameterTypes()).stream()
                .map(PersistenceAdapterCoverageTest::sampleArgument)
                .toArray();
    }

    private static Object sampleArgument(Class<?> type) {
        try {
            return PersistenceMapperCoverageTest.sample(type);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static <T> T mockRepository(Class<T> repositoryType, Class<?> entityType) {
        Answer<Object> answer = invocation -> {
            if (invocation.getMethod().getName().equals("save") && invocation.getArguments().length > 0) {
                return invocation.getArgument(0);
            }
            var returnType = invocation.getMethod().getReturnType();
            if (returnType.equals(Optional.class)) {
                return Optional.of(PersistenceMapperCoverageTest.sample(entityType));
            }
            if (returnType.equals(List.class)) {
                return List.of(PersistenceMapperCoverageTest.sample(entityType));
            }
            if (returnType.equals(boolean.class) || returnType.equals(Boolean.class)) {
                return true;
            }
            if (returnType.equals(void.class)) {
                return null;
            }
            if (entityType.isAssignableFrom(returnType)) {
                return invocation.getArguments().length > 0 ? invocation.getArgument(0) : PersistenceMapperCoverageTest.sample(entityType);
            }
            return Mockito.RETURNS_DEFAULTS.answer(invocation);
        };
        return Mockito.mock(repositoryType, answer);
    }
}
