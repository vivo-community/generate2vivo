package eu.tib.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import eu.tib.exception.ConfigLoadingException;
import eu.tib.exception.SparqlExecutionException;
import eu.tib.exception.SparqlParsingException;
import eu.tib.exception.StreamManagerException;
import eu.tib.utils.ResourceUtils;
import fr.mines_stetienne.ci.sparql_generate.FileConfigurations;
import fr.mines_stetienne.ci.sparql_generate.SPARQLExt;
import fr.mines_stetienne.ci.sparql_generate.engine.PlanFactory;
import fr.mines_stetienne.ci.sparql_generate.engine.RootPlan;
import fr.mines_stetienne.ci.sparql_generate.query.SPARQLExtQuery;
import fr.mines_stetienne.ci.sparql_generate.stream.LocationMapperAccept;
import fr.mines_stetienne.ci.sparql_generate.stream.LocatorClassLoaderAccept;
import fr.mines_stetienne.ci.sparql_generate.stream.SPARQLExtStreamManager;
import fr.mines_stetienne.ci.sparql_generate.utils.ContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.query.*;
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
        String confFilePath = confPath + File.separator + CONF_FILE;
        FileConfigurations config = readConfig(confFilePath);

        log.info("Read-in Generate Query for " + config.query);
        String queryPath = confPath + File.separator + config.query;
        SPARQLExtQuery query = parseSparqlGenerateQuery(queryPath, config);

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

    public FileConfigurations readConfig(String confFilePath) {
        try {
            String conf = new ResourceUtils().readResource(confFilePath);
            FileConfigurations config = (new Gson()).fromJson(conf, FileConfigurations.class);
            return config;
        } catch (IOException | NullPointerException | JsonSyntaxException e) {
            log.error("Error while reading the config file.", e);
            throw new ConfigLoadingException(GeneratePipeline.class, "configFile", confFilePath);
        }
    }

    public SPARQLExtQuery parseSparqlGenerateQuery(String queryPath, FileConfigurations config) {
        try {
            String sparqlGenerateString = new ResourceUtils().readResource(queryPath);
            SPARQLExtQuery q = (SPARQLExtQuery) QueryFactory.create(sparqlGenerateString, config.base, SPARQLExt.SYNTAX);
            if (!q.explicitlySetBaseURI() && config.base != null) q.setBaseURI(config.base);
            return q;
        } catch (IOException | NullPointerException e) {
            log.error(String.format("No query file %s was found.", queryPath), e);
            throw new SparqlParsingException(GeneratePipeline.class, "queryName", queryPath);
        } catch (QueryException e) {
            log.error(String.format("Query %s could not be parsed.", queryPath), e);
            throw new SparqlParsingException(GeneratePipeline.class, "queryName", queryPath);
        }
    }

    public Dataset getDataset(String confPath, FileConfigurations config) {
        Dataset ds = DatasetFactory.create();

        if (config.graph != null) {
            try {
                String dsPath = confPath + File.separator + config.graph;
                Model model = ModelFactory.createDefaultModel();
                RDFDataMgr.read(model, new ResourceUtils().getStreamForResource(dsPath), Lang.TTL);
                ds.setDefaultModel(model);
                log.info("Loaded Graph from resource " + config.graph);
            } catch (Exception ex) {
                log.debug("Graph could not be loaded: " + ex.getMessage());
            }
        }

        if (config.namedgraphs != null) {
            config.namedgraphs.forEach((ng) -> {
                try {
                    String ngPath = confPath + File.separator + ng.path;
                    Model model = ModelFactory.createDefaultModel();
                    RDFDataMgr.read(model, new ResourceUtils().getStreamForResource(ngPath), Lang.TTL);
                    ds.addNamedModel(ng.uri, model);
                    log.info("Loaded named graph from resource " + ng.path);
                } catch (Exception ex) {
                    log.debug("Cannot load named graph " + ng.path + ": " + ex.getMessage());
                }
            });
        }
        return ds;
    }

    public SPARQLExtStreamManager prepareStreamManager(String confPath, FileConfigurations config) {
        LocatorClassLoaderAccept locator = new LocatorClassLoaderAccept(getClass().getClassLoader());
        LocationMapperAccept mapper = new LocationMapperAccept();
        SPARQLExtStreamManager sm = SPARQLExtStreamManager.makeStreamManager(locator);
        sm.setLocationMapper(mapper);

        if (config.namedqueries != null) {
            log.debug("Mapping namedqueries..");
            config.namedqueries.forEach((doc) -> {
                String docpath = confPath + File.separator + doc.path;
                try {
                    mapper.addAltEntry(doc.uri, docpath);
                } catch (Exception e) {
                    log.error(String.format("No named query was found at %s.", docpath), e);
                    throw new StreamManagerException(GeneratePipeline.class, "namedQuery", docpath);
                }
            });
        }
        if (config.documentset != null) {
            log.debug("Mapping documentset..");
            config.documentset.forEach((doc) -> {
                String docpath = confPath + File.separator + doc.path;
                try {
                    mapper.addAltEntry(doc.uri, docpath);
                } catch (Exception e) {
                    log.error(String.format("No documentset was found at %s.", docpath), e);
                    throw new StreamManagerException(GeneratePipeline.class, "documentset", docpath);
                }
            });
        }
        if (config.namedgraphs != null) {
            log.debug("Mapping namedgraphs..");
            config.namedgraphs.forEach((doc) -> {
                String docpath = confPath + File.separator + doc.path;
                try {
                    mapper.addAltEntry(doc.uri, docpath);
                } catch (Exception e) {
                    log.error(String.format("No named graph was found at %s.", docpath), e);
                    throw new StreamManagerException(GeneratePipeline.class, "namedGraph", docpath);
                }
            });
        }

        return sm;
    }

    public List<Binding> input2Bindings(Map<String, String> input) {
        if (input == null) return null;

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