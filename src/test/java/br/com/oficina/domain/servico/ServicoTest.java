package br.com.oficina.domain.servico;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Catálogo de Serviços")
@Feature("Domínio — Serviço")
class ServicoTest {

    @Test
    @Story("Criar serviço com construtor")
    void deveCriarComConstrutor() {
        var servico = new Servico("Alinhamento", "Alinhamento 3D", new BigDecimal("120"), 45);

        assertEquals("Alinhamento", servico.getNome());
        assertEquals("Alinhamento 3D", servico.getDescricao());
        assertEquals(new BigDecimal("120"), servico.getValorUnitario());
        assertEquals(45, servico.getTempoEstimadoMinutos());
    }

    @Test
    @Story("Atualizar dados do serviço")
    void deveAtualizar() {
        var servico = new Servico("Troca oleo", null, new BigDecimal("100"), 20);
        servico.atualizar("Balanceamento", "Roda dianteira", new BigDecimal("80"), 30);

        assertEquals("Balanceamento", servico.getNome());
        assertEquals("Roda dianteira", servico.getDescricao());
        assertEquals(new BigDecimal("80"), servico.getValorUnitario());
    }
}
