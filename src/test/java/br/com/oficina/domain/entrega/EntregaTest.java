package br.com.oficina.domain.entrega;

import org.junit.jupiter.api.Test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Execução Técnica")
@Feature("Domínio — Entrega")
class EntregaTest {

    @Test
    @Story("Criar entrega com status AGUARDANDO_LIBERACAO")
    void deveCriarComStatusAguardandoLiberacao() {
        var entrega = Entrega.criar(1L);
        assertEquals(StatusEntrega.AGUARDANDO_LIBERACAO, entrega.getStatus());
        assertEquals(1L, entrega.getOrdemDeServicoId());
        assertNull(entrega.getDataEntrega());
    }

    @Test
    @Story("Liberar veículo para retirada")
    void deveLiberarVeiculo() {
        var entrega = Entrega.criar(1L);
        entrega.liberarVeiculo();
        assertEquals(StatusEntrega.VEICULO_LIBERADO, entrega.getStatus());
    }

    @Test
    @Story("Rejeitar liberação já realizada")
    void naoDeveLiberarSeJaLiberado() {
        var entrega = Entrega.criar(1L);
        entrega.liberarVeiculo();
        assertThrows(IllegalStateException.class, entrega::liberarVeiculo);
    }

    @Test
    @Story("Entregar veículo ao cliente")
    void deveEntregarVeiculo() {
        var entrega = Entrega.criar(1L);
        entrega.liberarVeiculo();
        entrega.entregarVeiculo();
        assertEquals(StatusEntrega.VEICULO_ENTREGUE, entrega.getStatus());
        assertNotNull(entrega.getDataEntrega());
    }

    @Test
    @Story("Rejeitar entrega sem liberação prévia")
    void naoDeveEntregarSemLiberar() {
        var entrega = Entrega.criar(1L);
        assertThrows(IllegalStateException.class, entrega::entregarVeiculo);
    }

    @Test
    @Story("Registrar veículo em pátio")
    void deveRegistrarEmPatio() {
        var entrega = Entrega.criar(1L);
        entrega.liberarVeiculo();
        entrega.registrarEmPatio();
        assertEquals(StatusEntrega.VEICULO_EM_PATIO, entrega.getStatus());
    }

    @Test
    @Story("Rejeitar registro em pátio sem liberar")
    void naoDeveRegistrarEmPatioSemLiberar() {
        var entrega = Entrega.criar(1L);
        assertThrows(IllegalStateException.class, entrega::registrarEmPatio);
    }

    @Test
    @Story("Retirar veículo do pátio")
    void deveRetirarDoPatio() {
        var entrega = Entrega.criar(1L);
        entrega.liberarVeiculo();
        entrega.registrarEmPatio();
        entrega.retirarDoPatio();
        assertEquals(StatusEntrega.VEICULO_ENTREGUE, entrega.getStatus());
        assertNotNull(entrega.getDataEntrega());
    }

    @Test
    @Story("Rejeitar retirada sem estar no pátio")
    void naoDeveRetirarDoPatioSemEstarNoPatio() {
        var entrega = Entrega.criar(1L);
        entrega.liberarVeiculo();
        assertThrows(IllegalStateException.class, entrega::retirarDoPatio);
    }

    @Test
    void fluxoCompleto() {
        var entrega = Entrega.criar(1L);
        assertEquals(StatusEntrega.AGUARDANDO_LIBERACAO, entrega.getStatus());

        entrega.liberarVeiculo();
        assertEquals(StatusEntrega.VEICULO_LIBERADO, entrega.getStatus());

        entrega.entregarVeiculo();
        assertEquals(StatusEntrega.VEICULO_ENTREGUE, entrega.getStatus());
        assertNotNull(entrega.getDataEntrega());
    }
}
