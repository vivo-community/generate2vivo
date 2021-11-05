package eu.tib.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface WriteResultService {
    ResponseEntity<String> execute(String queryName, Map<String, String> input);
}
