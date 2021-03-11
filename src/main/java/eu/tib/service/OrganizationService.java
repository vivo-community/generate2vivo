package eu.tib.service;

import eu.tib.store.VIVOExport;
import eu.tib.store.VIVOProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Map;

@Slf4j
@Service
public class OrganizationService {

    @Autowired
    private VIVOExport vivoExport;

    @Autowired
    private VIVOProperties vivoProperties;

    public String getOrganizationPlusPersons(String queryName, Map variables) {

        log.info("Starting pipeline for " + queryName);
        GeneratePipeline pipeline = new GeneratePipeline();
        Model output = pipeline.run(queryName, variables);
        log.info("Finished pipeline for " + queryName);

        if (vivoProperties.isValid()) {
            log.info("Found VIVO properties");
            return vivoExport.addOrganizationPlusPersons(output, vivoProperties);
        } else {
            log.info("Returning JSON-LD");
            StringWriter stringWriter = new StringWriter();
            output.write(stringWriter, "JSON-LD");
            String dataJson = stringWriter.toString();
            return dataJson;
        }
    }
}
