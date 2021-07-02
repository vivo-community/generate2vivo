package eu.tib.controller;

import eu.tib.service.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(value = "/proxy")
@Api(value = "Controller", tags = {"proxy"})
public class VIVOProxyController {

    @Autowired
    private ResponseService responseService;

    @ApiOperation(value = "add organization to VIVO", notes = "This method adds an organization to VIVO.")
    @GetMapping(value = "/organization", produces = "application/json")
    public ResponseEntity<String> getOrganization(
            @ApiParam("unique id") @RequestParam String oid,
            @ApiParam("organization name") @RequestParam String oname,
            @ApiParam(value = "organization website") @RequestParam(required = false) Optional<String> owebsite) {

        final String id = "sparqlg/proxy/organization";
        log.info("Incoming Request for " + id + " with id: " + oid + " and name " + oname);
        StopWatch stopWatch = new StopWatch(id);
        stopWatch.start(id);

        Map<String, String> input = Map.of(
                "id", oid,
                "name", oname
        );
        if (owebsite.isPresent()) input.put("website", owebsite.get());

        ResponseEntity result = responseService.buildResponse(id, input);

        stopWatch.stop();
        log.info(id + " took " + stopWatch.getTotalTimeSeconds() + "s");
        return result;
    }
}
