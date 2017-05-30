package de.ahus1.springprometheus.rest;

import com.codahale.metrics.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Random;

/**
 * @author Alexander Schwartz 2016
 */
@Component
@Path("/")
@Slf4j
@RequiredArgsConstructor
public class RestEndpoint {

    private Random random = new Random();

    @Path("countedCall")
    @GET
    @Timed(absolute = true, name = "countedCallExample")
    public Response countedCall() throws InterruptedException {
        int delay = random.nextInt(200);
        log.info("method called, waiting {}", delay);
        Thread.sleep(delay);
        return Response.ok("waited: " + delay + " ms").build();
    }

}
