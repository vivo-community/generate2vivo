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
@RequestMapping(value = "/ror")
@Api(value = "Controller", tags = {"ror"})
public class RORController {

    @Autowired
    private WriteResultService wrService;

    @ApiOperation(value = "Retrieve data about an organization from ROR", notes = "This method gets data about an organization from ROR by passing a ROR id.")
    @GetMapping(value = "/organization", produces = "application/json")
    public ResponseEntity<String> getOrganization(
            @Valid @Pattern(regexp = InputValidator.ror)
            @ApiParam("Complete ROR URL consisting of https://ror.org/ plus id")
            @RequestParam String ror) {

        final String id = "sparqlg/ror/organization";
        log.info("Incoming Request for " + id + " with ror: " + ror);

        return wrService.execute(id, Collections.singletonMap("ror", ror));
    }

    @ApiOperation(value = "Retrieve data about an organization and all their sub-organizations from ROR", notes = "This method gets data about an organization and all their sub-organizations from ROR by passing a ROR id.")
    @GetMapping(value = "/organizationPlusChildren", produces = "application/json")
    public ResponseEntity<String> getOrganizationPlusChildren(
            @Valid @Pattern(regexp = InputValidator.ror)
            @ApiParam("Complete ROR URL consisting of https://ror.org/ plus id")
            @RequestParam String ror) {

        final String id = "sparqlg/ror/organizationPlusChildren";
        log.info("Incoming Request for " + id + " with ror: " + ror);

        return wrService.execute(id, Collections.singletonMap("ror", ror));
    }
}
