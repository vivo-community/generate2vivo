package eu.tib.output.rest;

import eu.tib.output.WriteResultService;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.Model;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Slf4j
@Service
@Profile("!vivo")
public class HttpResponseService implements WriteResultService {

    public ResponseEntity<String> write(Model result) {

        log.info("Returning JSON-LD");
        StringWriter stringWriter = new StringWriter();
        result.write(stringWriter, "JSON-LD");
        String dataJSON = stringWriter.toString();

        return ResponseEntity.status(HttpStatus.OK).body(dataJSON);
    }
}
