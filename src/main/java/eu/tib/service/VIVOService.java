package eu.tib.service;

import eu.tib.storage.VIVOExport;
import eu.tib.storage.VIVOProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.Map;

@Slf4j
@Service
@Profile("vivo")
public class VIVOService implements WriteResultService {

    @Autowired
    private VIVOExport vivoExport;

    @Autowired
    private VIVOProperties vivoProperties;

    public ResponseEntity<String> execute(String queryName, Map<String, String> input) {

        StopWatch stopWatch = new StopWatch(queryName);
        stopWatch.start(queryName);

        GeneratePipeline pipeline = new GeneratePipeline();
        Model result = pipeline.run(queryName, input);
        log.info("Finished pipeline for " + queryName);

        vivoExport.exportData(result, vivoProperties);
        stopWatch.stop();
        log.info(queryName + " took " + stopWatch.getTotalTimeSeconds() + "s");

        String msg = (result.isEmpty()) ? "No data was generated." : "SPARQL update accepted.";
        String statusJSON = String.format("{\"status\":\"%s\"}", msg);
        log.info(statusJSON);
        return ResponseEntity.status(HttpStatus.OK).body(statusJSON);
    }
}
