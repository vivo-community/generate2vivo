package eu.tib.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import eu.tib.error.ConfigLoadingException;
import eu.tib.error.SparqlExecutionException;
import eu.tib.error.SparqlParsingException;
import eu.tib.error.StreamManagerException;
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
        SPARQLExtStreamManager sm = prepareStreamManager(confPath, config);

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
        try {
            RootPlan plan = PlanFactory.create(query);
            Model output = plan.execGenerate(bindings, context);
            log.info("Finished executing query");
            return output;
        } catch (Exception e) {
            log.error("Error while executing SPARQL-Generate.", e);
            throw new SparqlExecutionException(GeneratePipeline.class);
        }
    }

    public FileConfigurations readConfig(String resPath2config) {
        try {
            String conf = new ResourceUtils().readResource(resPath2config);
            FileConfigurations config = (new Gson()).fromJson(conf, FileConfigurations.class);
            return config;
        } catch (IOException | JsonSyntaxException e) {
            log.error("Error while reading the config file.", e);
            throw new ConfigLoadingException(GeneratePipeline.class, "configFile", resPath2config);
        }
    }

    public SPARQLExtQuery parseSparqlGenerateQuery(String queryName, String base) {
        try {
            String sparqlGenerateString = new ResourceUtils().readResource(queryName);
            SPARQLExtQuery q = (SPARQLExtQuery) QueryFactory.create(sparqlGenerateString, base, SPARQLExt.SYNTAX);
            if (!q.explicitlySetBaseURI()) q.setBaseURI(base);
            return q;
        } catch (IOException | NullPointerException e) {
            log.error(String.format("No query file named %s was found.", queryName), e);
            throw new SparqlParsingException(GeneratePipeline.class, "queryName", queryName);
        }
    }

    public Dataset getDataset(String confPath, FileConfigurations config) {
        Dataset ds = DatasetFactory.create();
        ResourceUtils futils = new ResourceUtils();

        if (config.graph != null) {
            try {
                File dsFromResource = futils.resource2File(confPath + File.separator + config.graph);
                ds.setDefaultModel(RDFDataMgr.loadModel(dsFromResource.toString(), Lang.TTL));
            } catch (Exception ex) {
                log.debug("Graph could not be loaded: " + ex.getMessage());
            }
        }

        if (config.namedgraphs != null) {
            config.namedgraphs.forEach((ng) -> {
                try {
                    File ngFromResource = futils.resource2File(confPath + File.separator + ng.path);
                    Model model = RDFDataMgr.loadModel(ngFromResource.toString(), Lang.TTL);
                    ds.addNamedModel(ng.uri, model);
                } catch (Exception ex) {
                    log.debug("Cannot load named graph " + ng.path + ": " + ex.getMessage());
                }
            });
        }
        return ds;
    }

    public SPARQLExtStreamManager prepareStreamManager(String confPath, FileConfigurations config) {
        String resPath = System.getProperty("java.io.tmpdir");
        LocatorFileAccept locator = new LocatorFileAccept(resPath);
        LocationMapperAccept mapper = new LocationMapperAccept();
        SPARQLExtStreamManager sm = SPARQLExtStreamManager.makeStreamManager(locator);
        sm.setLocationMapper(mapper);

        ResourceUtils futils = new ResourceUtils();

        if (config.namedqueries != null) {
            log.debug("Mapping namedqueries..");
            config.namedqueries.forEach((doc) -> {
                String docpath = confPath + File.separator + doc.path;
                try {
                    File f = futils.resource2File(docpath);
                    mapper.addAltEntry(doc.uri, f.getAbsolutePath());
                } catch (IOException e) {
                    log.error(String.format("No named query was found at %s.", docpath), e);
                    throw new StreamManagerException(GeneratePipeline.class, "namedQuery", doc.toString());
                }
            });
        }
        if (config.documentset != null) {
            log.debug("Mapping documentset..");
            config.documentset.forEach((doc) -> {
                String docpath = confPath + File.separator + doc.path;
                try {
                    File f = futils.resource2File(docpath);
                    mapper.addAltEntry(doc.uri, f.getAbsolutePath());
                } catch (IOException e) {
                    log.error(String.format("No document was found at %s.", docpath), e);
                    throw new StreamManagerException(GeneratePipeline.class, "documentset", doc.toString());
                }
            });
        }
        if (config.namedgraphs != null) {
            log.debug("Mapping namedgraphs..");
            config.namedgraphs.forEach((doc) -> {
                String docpath = confPath + File.separator + doc.path;
                try {
                    File f = futils.resource2File(docpath);
                    mapper.addAltEntry(doc.uri, f.getAbsolutePath());
                } catch (IOException e) {
                    log.error(String.format("No named graph was found at %s.", docpath), e);
                    throw new StreamManagerException(GeneratePipeline.class, "namedGraph", doc.toString());
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
            log.debug("Creating initial binding : " + k + "-->" + v);
        });
        return Collections.singletonList(BindingUtils.asBinding(initialBinding));
    }
}