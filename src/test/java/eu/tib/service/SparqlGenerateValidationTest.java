package eu.tib.service;

import fr.mines_stetienne.ci.sparql_generate.SPARQLExt;
import fr.mines_stetienne.ci.sparql_generate.query.SPARQLExtQuery;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.QueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class SparqlGenerateValidationTest {

    final String resPath = "sparqlg/**/*.rqg";

    @Test
    void validateRQGFilesInResourceDir() throws IOException {
        // get all sparql-generate files in resources/sparql folder and check if valid
        ClassLoader classLoader = getClass().getClassLoader();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
        Resource[] resources = resolver.getResources(String.format("classpath:%s", resPath));

        for (Resource r : resources) {
            System.out.println("Checking query " + r.getFilename());
            InputStream resStream = r.getInputStream();
            String query = IOUtils.toString(resStream, StandardCharsets.UTF_8);
            assertDoesNotThrow(() -> (SPARQLExtQuery) QueryFactory.create(query, SPARQLExt.SYNTAX));
            resStream.close();
        }
    }
}
