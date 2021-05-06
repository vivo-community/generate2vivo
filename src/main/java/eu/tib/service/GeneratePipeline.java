package eu.tib.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import eu.tib.utils.ResourceUtils;
import fr.mines_stetienne.ci.sparql_generate.FileConfigurations;
import fr.mines_stetienne.ci.sparql_generate.SPARQLExt;
import fr.mines_stetienne.ci.sparql_generate.engine.PlanFactory;
import fr.mines_stetienne.ci.sparql_generate.engine.RootPlan;
import fr.mines_stetienne.ci.sparql_generate.query.SPARQLExtQuery;
import fr.mines_stetienne.ci.sparql_generate.stream.LocationMapperAccept;
import fr.mines_stetienne.ci.sparql_generate.stream.LocatorFileAccept;
import fr.mines_stetienne.ci.sparql_generate.stream.SPARQLExtStreamManager;
import fr.mines_stetienne.ci.sparql_generate.utils.ContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.binding.BindingUtils;
import org.apache.jena.sparql.util.Context;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class GeneratePipeline {

    private static final String CONF_FILE = "sparql-generate-conf.json";

    public Model run(String confPath, Map<String, String> input) {
        log.info("Read config for " + confPath);
        FileConfigurations config = readConfig(confPath + File.separator + CONF_FILE);

        log.info("Read-in Generate Query for " + config.query);
        String queryPath = confPath + File.separator + config.query;
        SPARQLExtQuery query = parseSparqlGenerateQuery(queryPath, config.base);

        log.info("Initialize stream manager");
        SPARQLExtStreamManager sm = null;
        try {
            sm = prepareStreamManager(confPath, config);
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("Create context");
        Dataset ds = getDataset(confPath, config);
        Context context = ContextUtils.build()
                .setBase(config.base)
                .setPrefixMapping(query)
                .setInputDataset(ds)
                .setStreamManager(sm)
                .build();

        List<Binding> bindings = input2Bindings(input);

        log.info("Create & execute plan");
        RootPlan plan = PlanFactory.create(query);
        Model output = plan.execGenerate(bindings, context);
        log.info("Finished executing query");

        return output;
    }

    public FileConfigurations readConfig(String resPath) {
        try {
            String conf = new ResourceUtils().readResource(resPath);
            FileConfigurations config = (new Gson()).fromJson(conf, FileConfigurations.class);
            return config;
        } catch (IOException ex) {
            log.warn("IOException while reading the config file.", ex);
        } catch (JsonSyntaxException ex) {
            log.warn("JSON Syntax exception while loading the location mapping model for the queryset.", ex);
        }

        throw new RuntimeException("Error while reading config.");
    }

    public SPARQLExtQuery parseSparqlGenerateQuery(String queryName, String base) {
        try {
            String sparqlGenerateString = new ResourceUtils().readResource(queryName);
            SPARQLExtQuery query = (SPARQLExtQuery) QueryFactory.create(sparqlGenerateString, base, SPARQLExt.SYNTAX);
            if (!query.explicitlySetBaseURI()) query.setBaseURI(base);
            return query;
        } catch (IOException | NullPointerException ex) {
            log.error(String.format("No query file named %s was found.", queryName), ex);
        }

        throw new RuntimeException("Error while parsing the query to be executed.");
    }

    public Dataset getDataset(String confPath, FileConfigurations config) {
        Dataset ds = DatasetFactory.create();

        if (config.graph != null) {
            try {
                File dsFromResource = new ResourceUtils().resource2File(confPath + File.separator + config.graph);
                ds.setDefaultModel(RDFDataMgr.loadModel(dsFromResource.toString(), Lang.TTL));
            } catch (Exception ex) {
                log.debug("Graph could not be loaded: " + ex.getMessage());
            }
        }

        if (config.namedgraphs != null) {
            config.namedgraphs.forEach((ng) -> {
                try {
                    File ngFromResource = new ResourceUtils().resource2File(confPath + File.separator + ng.path);
                    Model model = RDFDataMgr.loadModel(ngFromResource.toString(), Lang.TTL);
                    ds.addNamedModel(ng.uri, model);
                } catch (Exception ex) {
                    log.debug("Cannot load named graph " + ng.path + ": " + ex.getMessage());
                }
            });
        }
        return ds;
    }

    public SPARQLExtStreamManager prepareStreamManager(String confPath, FileConfigurations config) throws
            IOException {
        ResourceUtils futils = new ResourceUtils();
        String resPath = System.getProperty("java.io.tmpdir");

        LocatorFileAccept locator = new LocatorFileAccept(resPath);
        LocationMapperAccept mapper = new LocationMapperAccept();
        SPARQLExtStreamManager sm = SPARQLExtStreamManager.makeStreamManager(locator);
        sm.setLocationMapper(mapper);

        if (config.namedqueries != null) {
            log.debug("Mapping namedqueries..");
            config.namedqueries.forEach((doc) -> {
                try {
                    File nqAsResource = futils.resource2File(confPath + File.separator + doc.path);
                    mapper.addAltEntry(doc.uri, nqAsResource.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        if (config.documentset != null) {
            log.debug("Mapping documentset..");
            config.documentset.forEach((doc) -> {
                try {
                    File dsAsResource = futils.resource2File(confPath + File.separator + doc.path);
                    mapper.addAltEntry(doc.uri, dsAsResource.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        if (config.namedgraphs != null) {
            log.debug("Mapping namedgraphs..");
            config.namedgraphs.forEach((doc) -> {
                try {
                    File ngAsResource = futils.resource2File(confPath + File.separator + doc.path);
                    mapper.addAltEntry(doc.uri, ngAsResource.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        return sm;
    }

    public List<Binding> input2Bindings(Map<String, String> input) {
        // transfer input parameters into query via binding
        QuerySolutionMap initialBinding = new QuerySolutionMap();
        Model model = ModelFactory.createDefaultModel();
        input.forEach((k, v) -> {
            RDFNode literal = model.createLiteral(v);
            initialBinding.add(k, literal);
        });
        return Collections.singletonList(BindingUtils.asBinding(initialBinding));
    }
}