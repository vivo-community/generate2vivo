package eu.tib.controller;

import eu.tib.controller.validation.InputValidator;
import eu.tib.service.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(value = "/organizationPlusPersons", produces = "application/json")
    public ResponseEntity<String> getOrganizationPlusPersons(
            @Valid @Pattern(regexp = InputValidator.ror)
            @ApiParam("Complete ROR URL consisting of https://ror.org/ plus id")
            @RequestParam String ror) {

        final String id = "sparqlg/datacitecommons/orga2person";
        log.info("Incoming Request for " + id + " with ror: " + ror);
        StopWatch stopWatch = new StopWatch(id);
        stopWatch.start(id);

        ResponseEntity result = responseService.buildResponse(id, Collections.singletonMap("ror", ror));

        stopWatch.stop();
        log.info(id + " took " + stopWatch.getTotalTimeSeconds() + "s");
        return result;
    }

    @GetMapping(value = "/personPlusPublications", produces = "application/json")
    public ResponseEntity<String> getPersonPlusPublications(
            @Valid @Pattern(regexp = InputValidator.orcid)
            @ApiParam("Complete Orcid URL consisting of https://orcid.org/ plus id")
            @RequestParam String orcid) {

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
