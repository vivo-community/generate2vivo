package eu.tib.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.Model;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.StringWriter;
import java.util.Map;

@Slf4j
@Service
@Profile("!vivo")
public class HttpResponseService implements WriteResultService {

    public ResponseEntity<String> execute(String queryName, Map<String, String> input) {

        StopWatch stopWatch = new StopWatch(queryName);
        stopWatch.start(queryName);

        GeneratePipeline pipeline = new GeneratePipeline();
        Model result = pipeline.run(queryName, input);
        log.info("Finished pipeline for " + queryName);

        if (result.isEmpty()) log.info("No data was generated.");

        log.info("Returning JSON-LD");
        StringWriter stringWriter = new StringWriter();
        result.write(stringWriter, "JSON-LD");
        String dataJSON = stringWriter.toString();

        stopWatch.stop();
        log.info(queryName + " took " + stopWatch.getTotalTimeSeconds() + "s");
        return ResponseEntity.status(HttpStatus.OK).body(dataJSON);
    }
}
