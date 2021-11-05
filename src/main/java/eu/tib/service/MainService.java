package eu.tib.service;

import eu.tib.output.WriteResultService;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.Map;

@Slf4j
@Service
public class MainService {

    @Autowired
    private WriteResultService wrService;

    public ResponseEntity<String> execute(String queryName, Map<String, String> input) {

        StopWatch stopWatch = new StopWatch(queryName);
        stopWatch.start(queryName);

        GeneratePipeline pipeline = new GeneratePipeline();
        Model result = pipeline.run(queryName, input);
        log.info("Finished pipeline for " + queryName);

        if (result.isEmpty()) log.info("No data was generated.");
        ResponseEntity<String> response = wrService.write(result);

        stopWatch.stop();
        log.info(queryName + " took " + stopWatch.getTotalTimeSeconds() + "s");
        return response;
    }
}
