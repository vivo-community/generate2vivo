package eu.tib.storage;

import eu.tib.exception.VIVOExportException;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.Model;
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

    public void exportData(Model data, VIVOProperties vivo) {
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
                log.info("Error while exporting data to VIVO: " + response.statusCode());
                log.info(response.body());
                throw new VIVOExportException(VIVOExport.class, "errorCode", Integer.toString(response.statusCode()),
                        "errorMessage", response.body());
            }

        } catch (IOException | InterruptedException e) {
            log.error("Error while exporting data to VIVO", e);
            throw new VIVOExportException(VIVOExport.class, "error", e.getClass().getName());
        }
    }
}