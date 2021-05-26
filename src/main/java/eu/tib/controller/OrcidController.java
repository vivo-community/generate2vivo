package eu.tib.controller;

import eu.tib.service.ResponseService;
import io.swagger.annotations.Api;
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

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.Collections;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/orcid")
@Api(value = "Controller", tags = {"orcid"})
public class OrcidController {

    @Autowired
    private ResponseService responseService;

    @PostMapping(value = "/getCurrentEmployees", produces = "application/json")
    public ResponseEntity<String> getCurrentEmployees(
            @Valid @Pattern(regexp = "^Q[1-9]\\d*$")
            @ApiParam("Wikidata id for a research organization starting with Q")
            @RequestParam String wikidata) {

        final String id = "sparqlg/orcid/employees";
        log.info("Incoming Request for " + id + " with wikidata: " + wikidata);
        StopWatch stopWatch = new StopWatch(id);
        stopWatch.start(id);

        ResponseEntity result = responseService.buildResponse(id, Collections.singletonMap("wikidata", wikidata));

        stopWatch.stop();
        log.info(id + " took " + stopWatch.getTotalTimeSeconds() + "s");
        return result;
    }
}
