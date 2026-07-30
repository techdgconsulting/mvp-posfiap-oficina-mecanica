package br.com.oficina.domain.model;

import br.com.oficina.domain.valueobject.StatusDiagnostico;

import org.junit.jupiter.api.Test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Execução Técnica")
@Feature("Domínio — Diagnóstico")
class DiagnosticoTest {

    @Test
    @Story("Diagnóstico inicia como PENDENTE")
    void deveComecarPendente() {
        var diag = Diagnostico.builder().build();
        assertEquals(StatusDiagnostico.PENDENTE, diag.getStatus());
    }

    @Test
    @Story("Iniciar diagnóstico")
    void deveIniciar() {
        var diag = Diagnostico.builder().build();
        diag.iniciar();
        assertEquals(StatusDiagnostico.EM_ANDAMENTO, diag.getStatus());
        assertNotNull(diag.getDataDiagnostico());
    }

    @Test
    @Story("Rejeitar início duplicado de diagnóstico")
    void naoIniciarDuasVezes() {
        var diag = Diagnostico.builder().build();
        diag.iniciar();
        assertThrows(IllegalStateException.class, diag::iniciar);
    }

    @Test
    @Story("Identificar problema no diagnóstico")
    void identificarProblema() {
        var diag = Diagnostico.builder().build();
        diag.iniciar();
        diag.identificarProblema("Motor falhando em marcha lenta");
        assertEquals("Motor falhando em marcha lenta", diag.getDescricaoProblema());
    }

    @Test
    @Story("Rejeitar identificação de problema sem iniciar")
    void naoIdentificarSemIniciar() {
        var diag = Diagnostico.builder().build();
        assertThrows(IllegalStateException.class,
            () -> diag.identificarProblema("Problema qualquer"));
    }

    @Test
    @Story("Concluir diagnóstico")
    void concluir() {
        var diag = Diagnostico.builder().build();
        diag.iniciar();
        diag.concluir();
        assertEquals(StatusDiagnostico.CONCLUIDO, diag.getStatus());
    }

    @Test
    @Story("Rejeitar conclusão sem iniciar")
    void naoConcluirSemIniciar() {
        var diag = Diagnostico.builder().build();
        assertThrows(IllegalStateException.class, diag::concluir);
    }
}
