package eu.tib.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class ResourceUtils {

    public String readResourceNoWhitespace(String filename) throws IOException {
        return readResource(filename, "\n")
                .replaceAll("\\s+", " ");
    }

    public String readResource(String filename) throws IOException {
        return readResource(filename, "\n");
    }

    public String readResource(String filename, String delimiter) throws IOException {

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
             InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader);) {

            String content = reader.lines().collect(Collectors.joining(delimiter));
            return content;
        }
    }

    public InputStream getStreamForResource(String filename) {
        return getClass().getClassLoader().getResourceAsStream(filename);
    }
}
