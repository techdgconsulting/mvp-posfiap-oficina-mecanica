package br.com.oficina.domain.execucao;

import org.junit.jupiter.api.Test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Execução Técnica")
@Feature("Domínio — Execução")
class ExecucaoTest {

    @Test
    @Story("Criar execução com status AGUARDANDO")
    void deveCriarComStatusAguardando() {
        var exec = Execucao.criar(1L);
        assertEquals(StatusExecucao.AGUARDANDO, exec.getStatus());
        assertNotNull(exec.getDiagnostico());
        assertNull(exec.getPeriodoExecucao());
    }

    @Test
    @Story("Percorrer fluxo completo com diagnóstico")
    void fluxoCompletoComDiagnostico() {
        var exec = Execucao.criar(1L);

        exec.iniciarDiagnostico("mecanico-teste");
        assertEquals(StatusExecucao.EM_DIAGNOSTICO, exec.getStatus());

        exec.iniciarServico();
        assertEquals(StatusExecucao.EM_ANDAMENTO, exec.getStatus());
        assertNotNull(exec.getPeriodoExecucao());
        assertNotNull(exec.getPeriodoExecucao().getInicio());

        exec.finalizarServico();
        assertEquals(StatusExecucao.SERVICO_FINALIZADO, exec.getStatus());
        assertNotNull(exec.getPeriodoExecucao().getFim());
    }

    @Test
    @Story("Iniciar serviço diretamente do estado AGUARDANDO")
    void podeIniciarServicoDiretoDeAguardando() {
        var exec = Execucao.criar(1L);
        exec.iniciarServico();
        assertEquals(StatusExecucao.EM_ANDAMENTO, exec.getStatus());
    }

    @Test
    @Story("Rejeitar finalização sem iniício")
    void naoFinalizaSemIniciar() {
        var exec = Execucao.criar(1L);
        assertThrows(IllegalStateException.class, exec::finalizarServico);
    }

    @Test
    @Story("Rejeitar diagnóstico quando já em andamento")
    void naoDiagnosticaSeJaEmAndamento() {
        var exec = Execucao.criar(1L);
        exec.iniciarServico();
        assertThrows(IllegalStateException.class, () -> exec.iniciarDiagnostico("mecanico-teste"));
    }

    @Test
    @Story("Tempo de execução zero sem início")
    void tempoExecucaoZeroSemInicio() {
        var exec = Execucao.criar(1L);
        assertEquals(0, exec.calcularTempoExecucao().toMinutes());
    }

    @Test
    @Story("Tempo de execução positivo após início")
    void tempoExecucaoPositivoAposInicio() {
        var exec = Execucao.criar(1L);
        exec.iniciarServico();
        // dataInicio acabou de ser setado, então a duração deve ser >= 0
        assertTrue(exec.calcularTempoExecucao().toNanos() >= 0);
    }

    @Test
    @Story("Calcular tempo de execução com data fim preenchida")
    void tempoExecucaoComDataFimPreenchida() {
        var exec = Execucao.criar(1L);
        exec.iniciarServico();
        exec.finalizarServico();
        // com dataFim preenchido, usa o valor fixo
        assertTrue(exec.calcularTempoExecucao().toNanos() >= 0);
    }

    @Test
    @Story("Rejeitar re-início de serviço finalizado")
    void naoIniciarServicoPorqueJaFinalizado() {
        var exec = Execucao.criar(1L);
        exec.iniciarServico();
        exec.finalizarServico();
        assertThrows(IllegalStateException.class, exec::iniciarServico);
    }
}
