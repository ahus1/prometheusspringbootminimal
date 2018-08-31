package de.ahus1.springprometheus.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.health.CompositeHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * Export the health of the service as a metrics.
 * extended from: https://micrometer.io/docs/guide/healthAsGauge
 */
@Configuration
@EnableScheduling
class HealthMetricsConfiguration {
    private CompositeHealthIndicator healthIndicator;
    private volatile Health health;

    /**
     * Update the status.
     * instead of synchronous health check, run periodically to avoid prometheus to block in case the
     * health check uses a remote call without proper timeout
     */
    @Scheduled(initialDelay = 0, fixedDelay = 10000)
    public void updateHealthMetric() {
        health = healthIndicator.health();
    }

    public HealthMetricsConfiguration(HealthAggregator healthAggregator,
                                      List<HealthIndicator> healthIndicators,
                                      MeterRegistry registry) {

        healthIndicator = new CompositeHealthIndicator(healthAggregator);

        for (int i = 0; i < healthIndicators.size(); i++) {
            healthIndicator.addHealthIndicator(Integer.toString(i), healthIndicators.get(i));
        }

        registry.gauge("health", health, status -> {
            if(health == null) {
                return 0;
            }
            switch (health.getStatus().getCode()) {
                case "UP":
                    return 3;
                case "OUT_OF_SERVICE":
                    return 2;
                case "DOWN":
                    return 1;
                case "UNKNOWN":
                default:
                    return 0;
            }
        });

    }
}
