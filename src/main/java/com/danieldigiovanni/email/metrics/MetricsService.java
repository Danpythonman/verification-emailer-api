package com.danieldigiovanni.email.metrics;

import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private final MetricsRepository metricsRepository;

    public MetricsService(MetricsRepository metricsRepository) {
        this.metricsRepository = metricsRepository;
    }

    public void trackMetrics(Metrics metrics) {
        this.metricsRepository.save(metrics);
    }

}
