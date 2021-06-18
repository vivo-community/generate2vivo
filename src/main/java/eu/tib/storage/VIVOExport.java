package eu.tib.storage;

import eu.tib.exception.VIVOExportException;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Slf4j
@Repository
public class VIVOExport {

    private static final int CHUNK_SIZE = 2500;  // triples per 'chunk'

    public void exportData(Model data, VIVOProperties vivo) {
        if (data.isEmpty()) {
            log.info("No data was generated, Model is empty.");
        } else {
            exportInChunks(data, vivo);
        }
    }

    /**
     * method taken from https://github.com/WheatVIVO/datasources/blob/master/datasources/src/main/java/org/wheatinitiative/vivo/datasource/util/sparql/SparqlEndpoint.java
     * and modified to send chunk and free it for garbage collection
     **/
    public void exportInChunks(Model data, VIVOProperties vivo) {
        StmtIterator sit = data.listStatements();
        int i = 0;
        Model currentChunk = ModelFactory.createDefaultModel();

        while (sit.hasNext()) {
            currentChunk.add(sit.nextStatement());
            i++;

            if (i >= CHUNK_SIZE || !sit.hasNext()) {
                send2VIVO(currentChunk, vivo);
                //reset variables
                currentChunk = ModelFactory.createDefaultModel();
                i = 0;
            }
        }
    }

    public void send2VIVO(Model data, VIVOProperties vivo) {
        log.info("Writing " + data.size() + " new statements to VIVO");
        String sparqlInsertQuery = buildInsertQuery(data, vivo.getGraph());
        log.debug("Sparql Insert Query: \n" + sparqlInsertQuery);

        HttpRequest request = buildSparqlRequest(vivo, sparqlInsertQuery);
        send(request);
    }

    public String buildInsertQuery(Model data, String graph) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        data.write(out, "N-TRIPLE");

        String query =
                "INSERT DATA { GRAPH <" + graph + "> { \n" +
                        out +
                        "}}";

        return query;
    }

    public HttpRequest buildSparqlRequest(VIVOProperties vivo, String sparqlQuery) {

        String requestBody = "email=" + URLEncoder.encode(vivo.getEmail(), StandardCharsets.UTF_8)
                + "&password=" + URLEncoder.encode(vivo.getPassword(), StandardCharsets.UTF_8)
                + "&update=" + URLEncoder.encode(sparqlQuery, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(vivo.getURL()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return request;
    }

    public void send(HttpRequest request) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error(response.body());
                throw new VIVOExportException("Error while exporting data to VIVO: " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            throw new VIVOExportException("Error while exporting data to VIVO");
        }
    }
}