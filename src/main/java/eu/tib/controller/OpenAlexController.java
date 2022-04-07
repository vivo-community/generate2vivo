package eu.tib.controller;

import eu.tib.controller.validation.InputValidator;
import eu.tib.service.MainService;
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
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/openalex")
@Api(value = "Controller", tags = {"openalex"})
public class OpenAlexController {

    @Autowired
    private MainService mainService;

    @ApiOperation(value = "Retrieve data about a work from OpenAlex",
            notes = "This method gets data about a work from OpenAlex by passing an DOI.")
    @GetMapping(value = "/work", produces = "application/json")
    public ResponseEntity<String> getWork(
            @Valid @Pattern(regexp = InputValidator.doi)
            @ApiParam("DOI of the publication")
            @RequestParam String doi,
            @ApiParam("email for polite requests")
            @RequestParam(defaultValue = "") String email) {

        final String id = "sparqlg/openalex/work";
        log.info("Incoming Request for " + id + " with doi: " + doi);

        return mainService.execute(id, Map.of("doi", doi, "polite_mail", email));
    }
}
