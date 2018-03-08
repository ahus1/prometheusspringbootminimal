package de.ahus1.springprometheus.rest;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Alexander Schwartz 2018
 */
@Component
@Slf4j
public class ServiceClass {

    private Random random = new Random();

    private AtomicInteger atomicInteger = new AtomicInteger();

    // as of micrometer 1.0.1 histograms are not supported (yet), only percentiles
    @Timed(value = "doSomething", description = "this is doing something", percentiles = { 0.90, 0.95, 0.99 })
    // @Timed(value = "doSomething", description = "this is doing something", histogram = true)
    public int doSomething() throws InterruptedException {
        log.info("hi");
        int delay = random.nextInt(200);
        log.info("method called, waiting {}", delay);
        Thread.sleep(delay);
        return delay;
    }

    @HystrixCommand(commandKey = "externalCall", groupKey = "interfaceOne")
    public int callExternal() throws InterruptedException {
        log.info("ho");
        int count = atomicInteger.getAndIncrement();
        // add some random error to simulate network behavior
        if (count % 10 == 0) {
            throw new RuntimeException();
        }
        int delay = random.nextInt(200);
        log.info("method called, waiting {}", delay);
        Thread.sleep(delay);
        return delay;
    }
}
