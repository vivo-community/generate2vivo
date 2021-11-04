package eu.tib.service;

import eu.tib.storage.VIVOExport;
import eu.tib.storage.VIVOProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.StringWriter;
import java.util.Map;

@Slf4j
@Service
public class ResponseService {

    @Autowired
    private VIVOExport vivoExport;

    @Autowired
    private VIVOProperties vivoProperties;

    public ResponseEntity<String> buildResponse(String queryName, Map<String, String> input) {

        StopWatch stopWatch = new StopWatch(queryName);
        stopWatch.start(queryName);

        GeneratePipeline pipeline = new GeneratePipeline();
        Model result = pipeline.run(queryName, input);
        log.info("Finished pipeline for " + queryName);

        if (result.isEmpty()) log.info("No data was generated.");

        if (vivoProperties.isValid()) {
            log.info("Found VIVO properties");
            vivoExport.exportData(result, vivoProperties);
            stopWatch.stop();
            log.info(queryName + " took " + stopWatch.getTotalTimeSeconds() + "s");

            String msg = (result.isEmpty()) ? "No data was generated." : "SPARQL update accepted.";
            String statusJSON = String.format("{\"status\":\"%s\"}", msg);
            return ResponseEntity.status(HttpStatus.OK).body(statusJSON);
        } else {
            log.info("Returning JSON-LD");
            StringWriter stringWriter = new StringWriter();
            result.write(stringWriter, "JSON-LD");
            String dataJSON = stringWriter.toString();
            
            stopWatch.stop();
            log.info(queryName + " took " + stopWatch.getTotalTimeSeconds() + "s");
            return ResponseEntity.status(HttpStatus.OK).body(dataJSON);
        }
    }
}
