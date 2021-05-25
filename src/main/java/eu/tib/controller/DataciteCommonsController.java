package eu.tib.controller;

import eu.tib.exception.SparqlExecutionException;
import eu.tib.exception.SparqlParsingException;
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
@RequestMapping(value = "/datacitecommons")
@Api(value = "Controller", tags = {"datacitecommons"})
public class DataciteCommonsController {

    @Autowired
    private ResponseService responseService;

    @PostMapping(value = "/getOrganizationPlusPersons", produces = "application/json")
    public ResponseEntity<String> getOrganizationPlusPersons(
            @Valid @Pattern(regexp = "^https://ror.org/\\d{2}[a-z0-9]{5}\\d{2}")
            @ApiParam("Complete ROR URL consisting of https://ror.org/ plus id")
            @RequestParam String ror)
            throws SparqlParsingException, SparqlExecutionException {

        final String id = "sparqlg/datacitecommons/orga2person";
        log.info("Incoming Request for " + id + " with ror: " + ror);
        StopWatch stopWatch = new StopWatch(id);
        stopWatch.start(id);

        ResponseEntity result = responseService.buildResponse(id, Collections.singletonMap("ror", ror));

        stopWatch.stop();
        log.info(id + " took " + stopWatch.getTotalTimeSeconds() + "s");
        return result;
    }

    @PostMapping(value = "/getPersonPlusPublications", produces = "application/json")
    public ResponseEntity<String> getPersonPlusPublications(
            @Valid @Pattern(regexp = "^https://orcid.org/\\d{4}-\\d{4}-\\d{4}-\\d{4}")
            @ApiParam("Complete Orcid URL consisting of https://orcid.org/ plus id")
            @RequestParam String orcid)
            throws SparqlParsingException, SparqlExecutionException {

        final String id = "sparqlg/datacitecommons/person2publication";
        log.info("Incoming Request for " + id + " with orcid: " + orcid);
        StopWatch stopWatch = new StopWatch(id);
        stopWatch.start(id);

        ResponseEntity result = responseService.buildResponse(id, Collections.singletonMap("orcid", orcid));

        stopWatch.stop();
        log.info(id + " took " + stopWatch.getTotalTimeSeconds() + "s");
        return result;
    }
}
