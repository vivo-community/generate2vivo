package eu.tib.controller;

import eu.tib.controller.validation.InputValidator;
import eu.tib.service.ResponseService;
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
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/crossref")
@Api(value = "Controller", tags = {"crossref"})
public class CrossrefController {

    @Autowired
    private ResponseService responseService;

    @ApiOperation(value = "Retrieve data about a person and their works from Crossref", notes = "This method gets data about a person and their works from Crossref by passing an ORCID id.")
    @GetMapping(value = "/personPlusWorks", produces = "application/json")
    public ResponseEntity<String> getPersonPlusWorks(
            @Valid @Pattern(regexp = InputValidator.orcid)
            @ApiParam("Complete Orcid URL consisting of https://orcid.org/ plus id")
            @RequestParam String orcid,
            @ApiParam("email for polite requests")
            @RequestParam(defaultValue = "") String email) {

        final String id = "sparqlg/crossref/personPlusWorks";
        log.info("Incoming Request for " + id + " with orcid: " + orcid);

        final String CURSOR = "*"; // starting cursor for pagination
        return responseService.buildResponse(id, Map.of("orcid", orcid,
                "polite_mail", email,
                "cursor", CURSOR));
    }

    @ApiOperation(value = "Retrieve data about a work from Crossref", notes = "This method gets data about a work from Crossref by passing an DOI.")
    @GetMapping(value = "/work", produces = "application/json")
    public ResponseEntity<String> getWork(
            @Valid @Pattern(regexp = InputValidator.doi)
            @ApiParam("DOI of the publication")
            @RequestParam String doi,
            @ApiParam("email for polite requests")
            @RequestParam(defaultValue = "") String email) {

        final String id = "sparqlg/crossref/work";
        log.info("Incoming Request for " + id + " with doi: " + doi);

        return responseService.buildResponse(id, Map.of("doi", doi, "polite_mail", email));
    }
}
