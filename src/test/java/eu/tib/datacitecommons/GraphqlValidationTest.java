package eu.tib.datacitecommons;

import graphql.parser.Parser;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GraphqlValidationTest {

    final String resPath = "sparqlg/**/*.graphql";

    @Test
    void parseGraphQLFilesInResourceDir() throws IOException {
        // get all graphql files in resources/sparql folder and check if well formed
        ClassLoader classLoader = getClass().getClassLoader();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
        Resource[] resources = resolver.getResources(String.format("classpath:%s", resPath));

        Parser graphQLParser = new Parser();
        for (Resource r : resources) {
            System.out.println("Checking query " + r.getFilename());
            String query = IOUtils.toString(r.getInputStream(), StandardCharsets.UTF_8);
            assertDoesNotThrow(() -> graphQLParser.parseDocument(query));
        }
    }
}
