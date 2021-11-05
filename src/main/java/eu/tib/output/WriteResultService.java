package eu.tib.output;

import org.apache.jena.rdf.model.Model;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface WriteResultService {
    ResponseEntity<String> write(Model result);
}
