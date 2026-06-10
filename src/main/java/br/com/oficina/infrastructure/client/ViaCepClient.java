package br.com.oficina.infrastructure.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ViaCepClient {

    private final RestTemplate restTemplate;

    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    public Optional<ViaCepResponse> buscarPorCep(String cep) {
        String cepLimpo = cep.replaceAll("[^\\d]", "");
        if (cepLimpo.length() != 8) {
            return Optional.empty();
        }
        try {
            var response = restTemplate.getForObject(VIA_CEP_URL, ViaCepResponse.class, cepLimpo);
            if (response == null || Boolean.TRUE.equals(response.erro())) {
                return Optional.empty();
            }
            return Optional.of(response);
        } catch (RestClientException e) {
            log.warn("Erro ao consultar ViaCEP para CEP {}: {}", cepLimpo, e.getMessage());
            return Optional.empty();
        }
    }
}
