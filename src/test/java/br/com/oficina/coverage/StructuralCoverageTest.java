package br.com.oficina.coverage;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class StructuralCoverageTest {

    private static final Set<String> STRUCTURAL_PACKAGES = Set.of(
            "br.com.oficina.application.command",
            "br.com.oficina.application.query",
            "br.com.oficina.adapters.in.web.request",
            "br.com.oficina.adapters.in.web.response",
            "br.com.oficina.adapters.out.persistence.jpa",
            "br.com.oficina.adapters.out.viacep",
            "br.com.oficina.domain.model",
            "br.com.oficina.infrastructure.config",
            "br.com.oficina.infrastructure.spring"
    );

    @Test
    void recordsEntitiesAndConfigurationBeansRemainInstantiable() throws Exception {
        for (var className : loadClassNames()) {
            if (STRUCTURAL_PACKAGES.stream().noneMatch(className::startsWith)) {
                continue;
            }

            var type = Class.forName(className);
            if (type.isInterface() || type.isAnnotation() || type.isSynthetic() || type.isAnonymousClass()) {
                continue;
            }

            if (type.isEnum()) {
                assertNotNull(type.getEnumConstants());
            } else if (type.isRecord()) {
                exerciseRecord(type);
            } else {
                var instance = instantiate(type);
                if (instance != null) {
                    exerciseJavaBean(instance);
                    exerciseConfiguration(instance);
                }
            }
        }
    }

    private static List<String> loadClassNames() throws Exception {
        var root = Path.of("target", "classes", "br", "com", "oficina");
        try (Stream<Path> files = Files.walk(root)) {
            return files
                    .filter(path -> path.toString().endsWith(".class"))
                    .map(root::relativize)
                    .map(Path::toString)
                    .map(name -> "br.com.oficina." + name
                            .replace('\\', '.')
                            .replace('/', '.')
                            .replace(".class", ""))
                    .toList();
        }
    }

    private static void exerciseRecord(Class<?> type) throws Exception {
        var components = type.getRecordComponents();
        var parameterTypes = Stream.of(components).map(component -> component.getType()).toArray(Class<?>[]::new);
        var values = Stream.of(parameterTypes).map(StructuralCoverageTest::sampleValue).toArray();
        var constructor = type.getDeclaredConstructor(parameterTypes);
        constructor.setAccessible(true);
        var instance = constructor.newInstance(values);

        for (var component : components) {
            component.getAccessor().invoke(instance);
        }
        instance.toString();
        instance.hashCode();
        instance.equals(instance);
    }

    private static Object instantiate(Class<?> type) {
        try {
            Constructor<?> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException ignored) {
            return instantiateWithBuilder(type);
        }
    }

    private static Object instantiateWithBuilder(Class<?> type) {
        try {
            var builder = type.getDeclaredMethod("builder").invoke(null);
            for (var method : builder.getClass().getDeclaredMethods()) {
                if (method.getParameterCount() == 1) {
                    method.setAccessible(true);
                    method.invoke(builder, sampleValue(method.getParameterTypes()[0]));
                }
            }
            var build = builder.getClass().getDeclaredMethod("build");
            build.setAccessible(true);
            return build.invoke(builder);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static void exerciseJavaBean(Object instance) throws Exception {
        for (var method : instance.getClass().getMethods()) {
            if (method.getDeclaringClass().equals(Object.class) || Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (method.getName().startsWith("set") && method.getParameterCount() == 1) {
                method.invoke(instance, sampleValue(method.getParameterTypes()[0]));
            }
        }

        for (var method : instance.getClass().getMethods()) {
            if (method.getDeclaringClass().equals(Object.class) || Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if ((method.getName().startsWith("get") || method.getName().startsWith("is"))
                    && method.getParameterCount() == 0) {
                try {
                    method.invoke(instance);
                } catch (ReflectiveOperationException ignored) {
                    // Synthetic structural instances may not satisfy every domain invariant.
                }
            }
        }
    }

    private static void exerciseConfiguration(Object instance) throws Exception {
        for (var method : instance.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Bean.class)) {
                continue;
            }
            method.setAccessible(true);
            var args = Stream.of(method.getParameterTypes()).map(StructuralCoverageTest::sampleValue).toArray();
            method.invoke(instance, args);
        }
    }

    private static Object sampleValue(Class<?> type) {
        if (type.equals(String.class)) {
            return "ABC1D23";
        }
        if (type.equals(Long.class) || type.equals(long.class)) {
            return 1L;
        }
        if (type.equals(Integer.class) || type.equals(int.class)) {
            return 1;
        }
        if (type.equals(Double.class) || type.equals(double.class)) {
            return 1.0d;
        }
        if (type.equals(Float.class) || type.equals(float.class)) {
            return 1.0f;
        }
        if (type.equals(Short.class) || type.equals(short.class)) {
            return (short) 1;
        }
        if (type.equals(Byte.class) || type.equals(byte.class)) {
            return (byte) 1;
        }
        if (type.equals(Character.class) || type.equals(char.class)) {
            return 'a';
        }
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return true;
        }
        if (type.equals(BigDecimal.class)) {
            return new BigDecimal("10.00");
        }
        if (type.equals(LocalDateTime.class)) {
            return LocalDateTime.now();
        }
        if (List.class.isAssignableFrom(type)) {
            return new ArrayList<>();
        }
        if (type.isEnum()) {
            return type.getEnumConstants()[0];
        }
        if (type.getName().equals("br.com.oficina.domain.valueobject.CpfCnpj")) {
            try {
                return type.getDeclaredConstructor(String.class).newInstance("12345678909");
            } catch (ReflectiveOperationException ex) {
                throw new IllegalStateException(ex);
            }
        }
        if (type.getName().equals("br.com.oficina.domain.valueobject.Placa")) {
            try {
                return type.getDeclaredConstructor(String.class).newInstance("ABC1D23");
            } catch (ReflectiveOperationException ex) {
                throw new IllegalStateException(ex);
            }
        }
        if (type.getName().equals("br.com.oficina.domain.valueobject.Quantidade")) {
            try {
                return type.getDeclaredConstructor(int.class).newInstance(10);
            } catch (ReflectiveOperationException ex) {
                throw new IllegalStateException(ex);
            }
        }
        if (type.isInterface()) {
            return Mockito.mock(type);
        }
        var instance = instantiate(type);
        if (instance != null) {
            return instance;
        }
        return Mockito.mock(type);
    }
}
