package de.ahus1.springprometheus.metrics;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.hotspot.GarbageCollectorExports;
import io.prometheus.client.hotspot.MemoryPoolsExports;
import io.prometheus.client.hotspot.VersionInfoExports;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Alexander Schwartz 2016.
 */
@Configuration
public class MetricsApplicationConfig {

    // as of now, this aspect needs to be created manually, see
    // https://github.com/micrometer-metrics/micrometer/issues/361
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    public MetricsApplicationConfig(PrometheusMeterRegistry prometheusMeterRegistry) {
        // add existing Prometheus modules as needed
        // beware: they will be visible in prometheus, but not in any other backend you might connect
        new MemoryPoolsExports().register(prometheusMeterRegistry.getPrometheusRegistry());
        new GarbageCollectorExports().register(prometheusMeterRegistry.getPrometheusRegistry());
        new VersionInfoExports().register(prometheusMeterRegistry.getPrometheusRegistry());

        new JvmGcMetrics().register(prometheusMeterRegistry.getPrometheusRegistry());

        /*
        // add prometheus-hystrix module as a replacement of the built-in micrometer hystrix metrics
        HystrixPrometheusMetricsPublisher.builder()
                .shouldExportDeprecatedMetrics(false)
                .withRegistry(prometheusMeterRegistry.getPrometheusRegistry())
                .buildAndRegister();
         */
    }

}
