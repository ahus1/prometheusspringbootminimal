package de.ahus1.springprometheus.rest;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * @author Alexander Schwartz 2016
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class RestEndpoint {

    private final ServiceClass serviceClass;

    private Random random = new Random();

    @GetMapping("/countedCall")
    @Timed(histogram = true) // customize this to use a histogram
    public String countedCall() throws InterruptedException {
        int delay = 0;
        delay += serviceClass.doSomething();
        delay += serviceClass.callExternal();
        return "waited: " + delay + " ms";
    }

}
