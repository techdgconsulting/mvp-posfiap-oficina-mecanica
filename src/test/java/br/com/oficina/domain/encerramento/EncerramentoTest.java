package br.com.oficina.domain.encerramento;

import org.junit.jupiter.api.Test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Execução Técnica")
@Feature("Domínio — Encerramento")
class EncerramentoTest {

    @Test
    @Story("Criar encerramento com status PENDENTE")
    void deveCriarComStatusPendente() {
        var enc = Encerramento.criar(1L);
        assertEquals(StatusEncerramento.PENDENTE, enc.getStatus());
        assertEquals(1L, enc.getOrdemDeServicoId());
        assertNull(enc.getDataEncerramento());
    }

    @Test
    @Story("Encerrar OS")
    void deveEncerrar() {
        var enc = Encerramento.criar(1L);
        enc.encerrar();
        assertEquals(StatusEncerramento.ENCERRADA, enc.getStatus());
        assertNotNull(enc.getDataEncerramento());
    }

    @Test
    @Story("Rejeitar encerramento duplo")
    void naoDeveEncerrarDuasVezes() {
        var enc = Encerramento.criar(1L);
        enc.encerrar();
        assertThrows(IllegalStateException.class, enc::encerrar);
    }
}
