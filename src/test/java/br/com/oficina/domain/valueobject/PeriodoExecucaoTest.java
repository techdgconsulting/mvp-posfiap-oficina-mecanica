package br.com.oficina.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Execução Técnica")
@Feature("Domínio — Período de Execução")
class PeriodoExecucaoTest {

    @Test
    @Story("Criar período com data de início")
    void deveCriarComInicio() {
        var inicio = LocalDateTime.of(2026, 5, 15, 10, 0);
        var periodo = new PeriodoExecucao(inicio);
        assertEquals(inicio, periodo.getInicio());
        assertNull(periodo.getFim());
        assertFalse(periodo.isFinalizado());
    }

    @Test
    @Story("Rejeitar período com início nulo")
    void naoDeveCriarComInicioNulo() {
        assertThrows(IllegalArgumentException.class, () -> new PeriodoExecucao(null));
    }

    @Test
    @Story("Finalizar período com data fim")
    void deveFinalizarComDataFim() {
        var inicio = LocalDateTime.of(2026, 5, 15, 10, 0);
        var fim = LocalDateTime.of(2026, 5, 15, 12, 30);
        var periodo = new PeriodoExecucao(inicio);

        periodo.finalizarEm(fim);

        assertEquals(fim, periodo.getFim());
        assertTrue(periodo.isFinalizado());
    }

    @Test
    @Story("Rejeitar finalização com fim nulo")
    void naoDeveFinalizarComFimNulo() {
        var periodo = new PeriodoExecucao(LocalDateTime.now());
        assertThrows(IllegalArgumentException.class, () -> periodo.finalizarEm(null));
    }

    @Test
    @Story("Rejeitar fim anterior ao início")
    void naoDeveFinalizarComFimAnteriorAoInicio() {
        var inicio = LocalDateTime.of(2026, 5, 15, 12, 0);
        var fimAntes = LocalDateTime.of(2026, 5, 15, 10, 0);
        var periodo = new PeriodoExecucao(inicio);

        assertThrows(IllegalArgumentException.class, () -> periodo.finalizarEm(fimAntes));
    }

    @Test
    @Story("Calcular duração com data fim")
    void deveCalcularDuracaoComFim() {
        var inicio = LocalDateTime.of(2026, 5, 15, 10, 0);
        var fim = LocalDateTime.of(2026, 5, 15, 12, 30);
        var periodo = new PeriodoExecucao(inicio);
        periodo.finalizarEm(fim);

        Duration duracao = periodo.calcularDuracao();
        assertEquals(150, duracao.toMinutes()); // 2h30 = 150 min
    }

    @Test
    @Story("Calcular duração sem fim usando momento atual")
    void deveCalcularDuracaoSemFimUsandoAgora() {
        var periodo = new PeriodoExecucao(LocalDateTime.now().minusMinutes(5));
        Duration duracao = periodo.calcularDuracao();
        assertTrue(duracao.toMinutes() >= 4); // pelo menos 4 min
    }

    @Test
    void periodosIguaisDevemSerEquals() {
        var t = LocalDateTime.of(2026, 5, 15, 10, 0);
        var p1 = new PeriodoExecucao(t);
        var p2 = new PeriodoExecucao(t);
        assertEquals(p1, p2);
    }
}
