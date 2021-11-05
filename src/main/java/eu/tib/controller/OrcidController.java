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
@RequestMapping(value = "/orcid")
@Api(value = "Controller", tags = {"orcid"})
public class OrcidController {

    @Autowired
    private WriteResultService wrService;

    @ApiOperation(value = "Retrieve data about a person and their works from ORCID", notes = "This method gets data about a person and their works from ORCID by passing an ORCID id.")
    @GetMapping(value = "/personPlusWorks", produces = "application/json")
    public ResponseEntity<String> getPersonPlusWorks(
            @Valid @Pattern(regexp = InputValidator.orcid)
            @ApiParam("Complete Orcid URL consisting of https://orcid.org/ plus id")
            @RequestParam String orcid) {

        final String id = "sparqlg/orcid/personPlusWorks";
        log.info("Incoming Request for " + id + " with orcid: " + orcid);

        return wrService.execute(id, Collections.singletonMap("orcid", orcid));
    }


    @ApiOperation(value = "Retrieve data about an organization's current employees and their works from ORCID", notes = "This method gets data about an organization's current employees and their works from ORCID by passing a ROR id.")
    @GetMapping(value = "/currentEmployeesPlusWorks", produces = "application/json")
    public ResponseEntity<String> getCurrentEmployeesPlusWorks(
            @Valid @Pattern(regexp = InputValidator.ror)
            @ApiParam("Complete ROR URL consisting of https://ror.org/ plus id")
            @RequestParam String ror) {

        final String id = "sparqlg/orcid/currentEmployeesPlusWorks";
        log.info("Incoming Request for " + id + " with ror: " + ror);

        return wrService.execute(id, Collections.singletonMap("ror", ror));
    }
}
