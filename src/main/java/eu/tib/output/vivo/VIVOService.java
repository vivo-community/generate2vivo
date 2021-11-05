package eu.tib.output.vivo;

import eu.tib.output.WriteResultService;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("vivo")
public class VIVOService implements WriteResultService {

    @Autowired
    private VIVOExport vivoExport;

    @Autowired
    private VIVOProperties vivoProperties;

    public ResponseEntity<String> write(Model result) {

        vivoExport.exportData(result, vivoProperties);
        String msg = (result.isEmpty()) ? "No data was generated." : "SPARQL update accepted.";
        String statusJSON = String.format("{\"status\":\"%s\"}", msg);
        log.info(statusJSON);

        return ResponseEntity.status(HttpStatus.OK).body(statusJSON);
    }
}
