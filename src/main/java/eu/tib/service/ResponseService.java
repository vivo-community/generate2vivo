package eu.tib.service;

import eu.tib.storage.VIVOExport;
import eu.tib.storage.VIVOProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Map;

@Slf4j
@Service
public class ResponseService {

    @Autowired
    private VIVOExport vivoExport;

    @Autowired
    private VIVOProperties vivoProperties;

    public ResponseEntity<String> buildResponse(String queryName, Map<String,String> input) {

        log.info("Starting pipeline for " + queryName);
        GeneratePipeline pipeline = new GeneratePipeline();
        Model result = pipeline.run(queryName, input);
        log.info("Finished pipeline for " + queryName);

        if (vivoProperties.isValid()) {
            log.info("Found VIVO properties");
            vivoExport.exportData(result, vivoProperties);
            return ResponseEntity.status(HttpStatus.OK).body("{\"status\":\"SPARQL update accepted.\"}");
        } else {
            log.info("Returning JSON-LD");
            StringWriter stringWriter = new StringWriter();
            result.write(stringWriter, "JSON-LD");
            String dataJson = stringWriter.toString();
            return ResponseEntity.status(HttpStatus.OK).body(dataJson);
        }
    }
}
