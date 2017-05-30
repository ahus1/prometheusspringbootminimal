package de.ahus1.springprometheus.metrics;

import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import io.prometheus.client.hotspot.DefaultExports;
import io.prometheus.client.spring.boot.EnablePrometheusEndpoint;
import io.prometheus.client.spring.boot.EnableSpringBootMetricsCollector;
import org.springframework.context.annotation.Configuration;

/**
 * @author Alexander Schwartz 2016.
 */
@Configuration
@EnableMetrics(proxyTargetClass = true)
@EnableSpringBootMetricsCollector
@EnablePrometheusEndpoint
public class MetricsApplicationConfig extends MetricsConfigurerAdapter {

    public MetricsApplicationConfig() {
        DefaultExports.initialize();
    }

}
