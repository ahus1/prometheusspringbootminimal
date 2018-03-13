package de.ahus1.springprometheus.rest;

import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * @author Alexander Schwartz 2016
 */
@RestController
@RequestMapping("/api")
public class RestEndpoint {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(RestEndpoint.class);
    private final ServiceClass serviceClass;

    private Random random = new Random();

    @java.beans.ConstructorProperties({"serviceClass"})
    public RestEndpoint(ServiceClass serviceClass) {
        this.serviceClass = serviceClass;
    }

    @GetMapping("/countedCall")
    @Timed(histogram = true) // customize this to use a histogram
    public String countedCall() throws InterruptedException {
        int delay = 0;
        delay += serviceClass.doSomething();
        delay += serviceClass.callExternal();
        return "waited: " + delay + " ms";
    }

}
