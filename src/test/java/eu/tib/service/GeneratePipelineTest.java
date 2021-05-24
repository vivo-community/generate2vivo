package eu.tib.service;

import eu.tib.exception.ConfigLoadingException;
import eu.tib.exception.SparqlParsingException;
import fr.mines_stetienne.ci.sparql_generate.FileConfigurations;
import fr.mines_stetienne.ci.sparql_generate.stream.LocationMapperAccept;
import fr.mines_stetienne.ci.sparql_generate.stream.LookUpRequest;
import fr.mines_stetienne.ci.sparql_generate.stream.SPARQLExtStreamManager;
import org.apache.jena.query.Dataset;
import org.apache.jena.sparql.engine.binding.Binding;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratePipelineTest {

    private static final String confPath = "sparqlg/example";
    private static final String CONF_FILE = "sparql-generate-conf.json";
    private static FileConfigurations config;
    private static GeneratePipeline pip = new GeneratePipeline();

    @BeforeAll
    static void readConfig() {
        // read in example config
        String confFilePath = confPath + File.separator + CONF_FILE;
        config = pip.readConfig(confFilePath);
    }

    @Test
    void readConfigTest() {
        // check if config was read correctly and value was retrieved for config.base
        String expectedBase = "http://example.com/";
        String actualBase = config.base;
        assertEquals(expectedBase, actualBase);
    }

    @Test
    void failReadConfigTest() {
        String nonExistingPath = "xxxxxxxxx";
        assertThrows(ConfigLoadingException.class, () -> pip.readConfig(nonExistingPath));
    }

    @Test
    void parseSparqlGenerateQueryTest() {
        // check if query can be parsed and baseURI is set to config.base
        String queryPath = confPath + File.separator + config.query;
        assertDoesNotThrow(() -> pip.parseSparqlGenerateQuery(queryPath, config));
        assertEquals(config.base, pip.parseSparqlGenerateQuery(queryPath, config).getBaseURI());
    }

    @Test
    void failParseSparqlGenerateQueryTest() {
        String nonExistingPath = "xxxxxxxxx";
        assertThrows(SparqlParsingException.class, () -> pip.parseSparqlGenerateQuery(nonExistingPath, config));
        String notAQueryPath = confPath + File.separator + CONF_FILE;
        assertThrows(SparqlParsingException.class, () -> pip.parseSparqlGenerateQuery(notAQueryPath, config));
    }

    @Test
    void getDatasetTest() {
        Dataset ds = pip.getDataset(confPath, config);
        assertFalse(ds.isEmpty());
    }

    @Test
    void failGetDatasetTest() {
        String nonExistingPath = "xxxxxxxxx";
        Dataset ds = pip.getDataset(nonExistingPath, config);
        assertTrue(ds.isEmpty());
    }

    @Test
    void prepareStreamManagerTest() {
        SPARQLExtStreamManager sm = pip.prepareStreamManager(confPath, config);

        String uri = "https://example.com/test.json";
        String alt = confPath + File.separator + "test.json";

        LocationMapperAccept lm = (LocationMapperAccept) sm.getLocationMapper();
        LookUpRequest altEntry = lm.getAltEntry(new LookUpRequest(uri));
        assertEquals(alt, altEntry.getFilenameOrURI());
    }

    @Test
    void input2BindingsTest() {
        String key = "test";
        String value = "xyz";
        Map<String, String> input = Collections.singletonMap(key, value);
        List<Binding> bindings = pip.input2Bindings(input);

        String actual = bindings.get(0).toString();
        String expected = String.format("( ?%s = \"%s\" )", key, value);
        assertEquals(expected, actual);
    }
}
