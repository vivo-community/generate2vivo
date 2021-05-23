package eu.tib.storage;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class SparqlRequest {
    private HttpRequest request = null;

    public SparqlRequest(VIVOProperties vivo, String sparqlQuery) {

        String requestBody = "email=" + URLEncoder.encode(vivo.getEmail(), StandardCharsets.UTF_8)
                + "&password=" + URLEncoder.encode(vivo.getPassword(), StandardCharsets.UTF_8)
                + "&update=" + URLEncoder.encode(sparqlQuery, StandardCharsets.UTF_8);

        // @TODO error handling
        request = HttpRequest.newBuilder()
                .uri(URI.create(vivo.getURL()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    }

    public String send() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());

        if (response.statusCode() == 200) return response.body();
        else throw new IOException(); //@TODO handle if status code not 200
    }
}