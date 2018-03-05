package de.ahus1.springprometheus;

import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Alexander Schwartz 2016
 */
@Configuration
@ComponentScan(basePackages = {"de.ahus1.springprometheus"})
@EnableHystrix
public class SpringConfiguration {
}
