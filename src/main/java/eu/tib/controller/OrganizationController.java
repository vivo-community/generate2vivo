package eu.tib.controller;

import eu.tib.error.SparqlExecutionException;
import eu.tib.error.SparqlParsingException;
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
@RequestMapping(value = "/organization")
@Api(value = "Controller", tags = {"organization"})
public class OrganizationController {

    @Autowired
    private ResponseService responseService;

    @PostMapping(produces = "application/json")
    public ResponseEntity<String> getOrganizationPlusPersons(
            @Valid @Pattern(regexp = "^https://ror.org/\\d{2}[a-z0-9]{5}\\d{2}")
            @ApiParam("Complete ROR URL consisting of https://ror.org/ plus id")
            @RequestParam String ror)
            throws SparqlParsingException, SparqlExecutionException {

        final String id = "ror/orga2person";
        log.info("Incoming Request for " + id + " with ror: " + ror);
        StopWatch stopWatch = new StopWatch(id);
        stopWatch.start(id);

        ResponseEntity result = responseService.buildResponse(id, Collections.singletonMap("ror", ror));

        stopWatch.stop();
        log.info(id + " took " + stopWatch.getTotalTimeSeconds() + "s");
        return result;
    }
}
