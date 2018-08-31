package de.ahus1.springprometheus.rest;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alexander Schwartz 2016
 */
@RestController
@RequestMapping("/api")
public class RestEndpoint {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(RestEndpoint.class);
    private final ServiceClass serviceClass;
    private final Counter myOperationCounterSuccess;

    public RestEndpoint(ServiceClass serviceClass, MeterRegistry registry) {
        this.serviceClass = serviceClass;

        myOperationCounterSuccess = Counter
                .builder("myOperation")
                .description("a description for humans")
                .tags("result", "success")
                .register(registry);

    }

    @GetMapping("/countedCall")
    @Timed(histogram = true) // customize this to use a histogram
    public String countedCall() throws InterruptedException {
        int delay = 0;
        delay += serviceClass.doSomething();
        delay += serviceClass.callExternal();
        return "waited: " + delay + " ms";
    }

    @GetMapping("/demo")
    public String apiUse() {
        myOperationCounterSuccess.increment();
        return "OK";
    }

}
