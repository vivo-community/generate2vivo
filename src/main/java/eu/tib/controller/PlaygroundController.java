package eu.tib.controller;

import eu.tib.service.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/playground")
@Api(value = "Controller", tags = {"playground"})
public class PlaygroundController {

    @Autowired
    private ResponseService responseService;

    @PostMapping(value = "/tryPlayground", produces = "application/json")
    public ResponseEntity<String> tryPlayground(
            @ApiParam(value = "body", example = "{\"id\":\"123\", \"name\":\"D.Morgendorffer\"}")
            @RequestParam String body) {

        final String id = "sparqlg/playground";
        log.info("Incoming Request for " + id + " with body: " + body);
        StopWatch stopWatch = new StopWatch(id);
        stopWatch.start(id);

        ResponseEntity result = responseService.buildResponse(id, Collections.singletonMap("body", body));

        stopWatch.stop();
        log.info(id + " took " + stopWatch.getTotalTimeSeconds() + "s");
        return result;
    }
}
