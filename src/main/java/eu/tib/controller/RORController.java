package eu.tib.controller;

import eu.tib.controller.validation.Validator;
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
@RequestMapping(value = "/ror")
@Api(value = "Controller", tags = {"ror"})
public class RORController {

    @Autowired
    private ResponseService responseService;

    @GetMapping(value = "/organizationPlusChildren", produces = "application/json")
    public ResponseEntity<String> getOrganizationPlusChildren(
            @Valid @Pattern(regexp = Validator.ror)
            @ApiParam("Complete ROR URL consisting of https://ror.org/ plus id")
            @RequestParam String ror) {

        final String id = "sparqlg/ror/orga2children";
        log.info("Incoming Request for " + id + " with ror: " + ror);
        StopWatch stopWatch = new StopWatch(id);
        stopWatch.start(id);

        ResponseEntity result = responseService.buildResponse(id, Collections.singletonMap("ror", ror));

        stopWatch.stop();
        log.info(id + " took " + stopWatch.getTotalTimeSeconds() + "s");
        return result;
    }

    @GetMapping(value = "/organization", produces = "application/json")
    public ResponseEntity<String> getOrganization(
            @Valid @Pattern(regexp = Validator.ror)
            @ApiParam("Complete ROR URL consisting of https://ror.org/ plus id")
            @RequestParam String ror) {

        final String id = "sparqlg/ror/organization";
        log.info("Incoming Request for " + id + " with ror: " + ror);
        StopWatch stopWatch = new StopWatch(id);
        stopWatch.start(id);

        ResponseEntity result = responseService.buildResponse(id, Collections.singletonMap("ror", ror));

        stopWatch.stop();
        log.info(id + " took " + stopWatch.getTotalTimeSeconds() + "s");
        return result;
    }
}
