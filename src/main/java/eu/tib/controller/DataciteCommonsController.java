package eu.tib.controller;

import eu.tib.controller.validation.InputValidator;
import eu.tib.service.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation(value = "Retrieve organization data from Datacite Commons", notes = "This method gets data about an organization from Datacite Commons by passing a ROR id.")
    @GetMapping(value = "/organization", produces = "application/json")
    public ResponseEntity<String> getOrganization(
            @Valid @Pattern(regexp = InputValidator.ror)
            @ApiParam("Complete ROR URL consisting of https://ror.org/ plus id")
            @RequestParam String ror) {

        final String id = "sparqlg/datacitecommons/organization";
        log.info("Incoming Request for " + id + " with ror: " + ror);
        StopWatch stopWatch = new StopWatch(id);
        stopWatch.start(id);

        ResponseEntity result = responseService.buildResponse(id, Collections.singletonMap("ror", ror));

        stopWatch.stop();
        log.info(id + " took " + stopWatch.getTotalTimeSeconds() + "s");
        return result;
    }

    @ApiOperation(value = "Retrieve data about an organization and its affiliated people from Datacite Commons", notes = "This method gets data about an organization and its affiliated people from Datacite Commons by passing a ROR id.")
    @GetMapping(value = "/organizationPlusPeople", produces = "application/json")
    public ResponseEntity<String> getOrganizationPlusPeople(
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
    @ApiOperation(value = "Retrieve data about an organization and its affiliated people and their respective works from Datacite Commons", notes = "This method gets data about an organization and its affiliated people and their respective works from Datacite Commons by passing a ROR id.")
    @GetMapping(value = "/organizationPlusPeoplePlusWorks", produces = "application/json")
    public ResponseEntity<String> getOrganizationPlusPeoplePlusWorks(
            @Valid @Pattern(regexp = InputValidator.ror)
            @ApiParam("Complete ROR URL consisting of https://ror.org/ plus id")
            @RequestParam String ror) {

        final String id = "sparqlg/datacitecommons/orga2person2publication";
        log.info("Incoming Request for " + id + " with ror: " + ror);
        StopWatch stopWatch = new StopWatch(id);
        stopWatch.start(id);

        ResponseEntity result = responseService.buildResponse(id, Collections.singletonMap("ror", ror));

        stopWatch.stop();
        log.info(id + " took " + stopWatch.getTotalTimeSeconds() + "s");
        return result;
    }

    @ApiOperation(value = "Retrieve data about a person from Datacite Commons", notes = "This method gets data about a person from Datacite Commons by passing an ORCID id.")
    @GetMapping(value = "/person", produces = "application/json")
    public ResponseEntity<String> getPerson(
            @Valid @Pattern(regexp = InputValidator.orcid)
            @ApiParam("Complete Orcid URL consisting of https://orcid.org/ plus id")
            @RequestParam String orcid) {

        final String id = "sparqlg/datacitecommons/person";
        log.info("Incoming Request for " + id + " with orcid: " + orcid);
        StopWatch stopWatch = new StopWatch(id);
        stopWatch.start(id);

        ResponseEntity result = responseService.buildResponse(id, Collections.singletonMap("orcid", orcid));

        stopWatch.stop();
        log.info(id + " took " + stopWatch.getTotalTimeSeconds() + "s");
        return result;
    }

    @ApiOperation(value = "Retrieve data about a person and their works from Datacite Commons", notes = "This method gets data about a person and their works from Datacite Commons by passing an ORCID id.")
    @GetMapping(value = "/personPlusWorks", produces = "application/json")
    public ResponseEntity<String> getPersonPlusWorks(
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

    @ApiOperation(value = "Retrieve data about a work from Datacite Commons", notes = "This method gets data about a work from Datacite Commons by passing an DOI.")
    @GetMapping(value = "/work", produces = "application/json")
    public ResponseEntity<String> getWork(
            @Valid @Pattern(regexp = InputValidator.doi)
            @ApiParam("DOI of the publication")
            @RequestParam String doi) {

        final String id = "sparqlg/datacitecommons/work";
        log.info("Incoming Request for " + id + " with doi: " + doi);
        StopWatch stopWatch = new StopWatch(id);
        stopWatch.start(id);

        ResponseEntity result = responseService.buildResponse(id, Collections.singletonMap("doi", doi));

        stopWatch.stop();
        log.info(id + " took " + stopWatch.getTotalTimeSeconds() + "s");
        return result;
    }
}
