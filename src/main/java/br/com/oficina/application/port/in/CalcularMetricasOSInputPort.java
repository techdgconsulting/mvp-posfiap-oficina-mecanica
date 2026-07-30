package br.com.oficina.application.port.in;

import br.com.oficina.application.query.MetricasOSResult;

public interface CalcularMetricasOSInputPort {
    MetricasOSResult executeMetricas(Long ordemServicoId);
}
