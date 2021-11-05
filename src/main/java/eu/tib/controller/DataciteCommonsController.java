package eu.tib.controller;

import eu.tib.controller.validation.InputValidator;
import eu.tib.service.WriteResultService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
    private WriteResultService wrService;

    @ApiOperation(value = "Retrieve organization data from Datacite Commons", notes = "This method gets data about an organization from Datacite Commons by passing a ROR id.")
    @GetMapping(value = "/organization", produces = "application/json")
    public ResponseEntity<String> getOrganization(
            @Valid @Pattern(regexp = InputValidator.ror)
            @ApiParam("Complete ROR URL consisting of https://ror.org/ plus id")
            @RequestParam String ror) {

        final String id = "sparqlg/datacitecommons/organization";
        log.info("Incoming Request for " + id + " with ror: " + ror);

        return wrService.execute(id, Collections.singletonMap("ror", ror));
    }

    @ApiOperation(value = "Retrieve data about an organization and its affiliated people from Datacite Commons", notes = "This method gets data about an organization and its affiliated people from Datacite Commons by passing a ROR id.")
    @GetMapping(value = "/organizationPlusPeople", produces = "application/json")
    public ResponseEntity<String> getOrganizationPlusPeople(
            @Valid @Pattern(regexp = InputValidator.ror)
            @ApiParam("Complete ROR URL consisting of https://ror.org/ plus id")
            @RequestParam String ror) {

        final String id = "sparqlg/datacitecommons/organizationPlusPeople";
        log.info("Incoming Request for " + id + " with ror: " + ror);

        return wrService.execute(id, Collections.singletonMap("ror", ror));
    }

    @ApiOperation(value = "Retrieve data about an organization and its affiliated people and their respective publications from Datacite Commons", notes = "This method gets data about an organization and its affiliated people and their respective publications from Datacite Commons by passing a ROR id.")
    @GetMapping(value = "/organizationPlusPeoplePlusPublications", produces = "application/json")
    public ResponseEntity<String> getOrganizationPlusPeoplePlusPublications(
            @Valid @Pattern(regexp = InputValidator.ror)
            @ApiParam("Complete ROR URL consisting of https://ror.org/ plus id")
            @RequestParam String ror) {

        final String id = "sparqlg/datacitecommons/organizationPlusPeoplePlusPublications";
        log.info("Incoming Request for " + id + " with ror: " + ror);

        return wrService.execute(id, Collections.singletonMap("ror", ror));
    }

    @ApiOperation(value = "Retrieve data about a person from Datacite Commons", notes = "This method gets data about a person from Datacite Commons by passing an ORCID id.")
    @GetMapping(value = "/person", produces = "application/json")
    public ResponseEntity<String> getPerson(
            @Valid @Pattern(regexp = InputValidator.orcid)
            @ApiParam("Complete Orcid URL consisting of https://orcid.org/ plus id")
            @RequestParam String orcid) {

        final String id = "sparqlg/datacitecommons/person";
        log.info("Incoming Request for " + id + " with orcid: " + orcid);

        return wrService.execute(id, Collections.singletonMap("orcid", orcid));
    }

    @ApiOperation(value = "Retrieve data about a person and their publications from Datacite Commons", notes = "This method gets data about a person and their publications from Datacite Commons by passing an ORCID id.")
    @GetMapping(value = "/personPlusPublications", produces = "application/json")
    public ResponseEntity<String> getPersonPlusPublications(
            @Valid @Pattern(regexp = InputValidator.orcid)
            @ApiParam("Complete Orcid URL consisting of https://orcid.org/ plus id")
            @RequestParam String orcid) {

        final String id = "sparqlg/datacitecommons/personPlusPublications";
        log.info("Incoming Request for " + id + " with orcid: " + orcid);

        return wrService.execute(id, Collections.singletonMap("orcid", orcid));
    }

    @ApiOperation(value = "Retrieve data about a work from Datacite Commons", notes = "This method gets data about a work from Datacite Commons by passing an DOI.")
    @GetMapping(value = "/work", produces = "application/json")
    public ResponseEntity<String> getWork(
            @Valid @Pattern(regexp = InputValidator.doi)
            @ApiParam("DOI of the publication")
            @RequestParam String doi) {

        final String id = "sparqlg/datacitecommons/work";
        log.info("Incoming Request for " + id + " with doi: " + doi);

        return wrService.execute(id, Collections.singletonMap("doi", doi));
    }
}
