package eu.tib.integrationTest;

import eu.tib.service.GeneratePipeline;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegrationTest {

    private static final String confPath = "sparqlg/example";

    @Test
    void test() throws IOException {
        GeneratePipeline pip = new GeneratePipeline();
        Model result = pip.run(confPath, null);

        StringWriter stringWriter = new StringWriter();
        result.write(stringWriter, "TTL");
        String actualData = stringWriter.toString();

        String expectedPath = confPath + File.separator + "expected_output.ttl";
        InputStream expectedStream = getClass().getClassLoader().getResourceAsStream(expectedPath);
        String expectedData = IOUtils.toString(expectedStream, StandardCharsets.UTF_8);
        System.out.println(expectedData);
        System.out.println(actualData);
        assertEquals(expectedData, actualData);
    }
}