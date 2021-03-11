package eu.tib.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class FileUtils {
    private static final String graphqlFilePath = "/graphql/%s.graphql";
    private static final String sparqlFilePath = "/sparql/%s.rqg";

    public String getSparqlQuery(String id) throws IOException {
        return readResource(String.format(sparqlFilePath, id));
    }

    public String getGraphqlQuery(String id) throws IOException {
        String resourcePath = String.format(graphqlFilePath, id);
        System.out.println(resourcePath);

        return readResourceNoWhitespace(resourcePath);
    }

    public String readResourceNoWhitespace(String filename) throws IOException {
        return readResource(filename, "\n")
                .replaceAll("\\s+", " ");
    }

    public String readResource(String filename) throws IOException {
        return readResource(filename, "\n");
    }

    public String readResource(String filename, String delimiter) throws IOException {

        try (InputStream inputStream = getClass().getResourceAsStream(filename);
             InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader);) {

            String content = reader.lines().collect(Collectors.joining(delimiter));
            return content;
        }
    }
}
