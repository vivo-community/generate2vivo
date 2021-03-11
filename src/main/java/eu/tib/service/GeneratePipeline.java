package eu.tib.service;

import com.lambdista.util.Try;
import eu.tib.error.GraphqlRequestException;
import eu.tib.error.SparqlExecutionException;
import eu.tib.error.SparqlParsingException;
import eu.tib.utils.FileUtils;
import eu.tib.utils.GraphqlRequest;
import fr.mines_stetienne.ci.sparql_generate.SPARQLExt;
import fr.mines_stetienne.ci.sparql_generate.engine.PlanFactory;
import fr.mines_stetienne.ci.sparql_generate.engine.RootPlan;
import fr.mines_stetienne.ci.sparql_generate.query.SPARQLExtQuery;
import fr.mines_stetienne.ci.sparql_generate.stream.LocationMapperAccept;
import fr.mines_stetienne.ci.sparql_generate.stream.LocatorURLAccept;
import fr.mines_stetienne.ci.sparql_generate.stream.SPARQLExtStreamManager;
import fr.mines_stetienne.ci.sparql_generate.utils.ContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.util.Context;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.Map;

@Slf4j
public class GeneratePipeline {

    public Model run(String queryName, Map variables) {

        log.info("Build GraphQL Get URI for query " + queryName+ " and variables "+variables.toString());
        Try<URI> graphqlGetURL = Try.apply(() -> buildGraphQLURL(queryName, variables));
        graphqlGetURL.failed().forEach(e -> {
            log.error("error while building GraphQL URL: ", e);
            throw new GraphqlRequestException(GeneratePipeline.class, queryName, variables.toString());
        });
        log.info("Finished building GraphQL Get URI");
        log.debug(graphqlGetURL.get().toString());

        log.info("Read-in Generate Query for " + queryName);
        Try<SPARQLExtQuery> generateQuery = Try.apply(() -> parseSparqlGenerateQuery(queryName));
        generateQuery.failed().forEach(e -> {
            log.error("error while parsing Sparql Query: ",e);
            throw new SparqlParsingException(GeneratePipeline.class, queryName, variables.toString());
        });
        log.info("Finished reading Generate Query");

        log.info("Run generate query");
        Try<Model> result = graphqlGetURL.flatMap(url ->
                generateQuery.flatMap(generate ->
                        Try.apply(() -> executeGenerate(url, generate))));
        result.failed().forEach(e -> {
            log.error("error while executing Sparql Query: ",e);
            throw new SparqlExecutionException(GeneratePipeline.class, queryName, variables.toString());
        });
        log.info("Finished running generate query");

        return result.get();
    }

    public URI buildGraphQLURL(String queryName, Map variables) throws IOException {
        // build GraphQL Query (HTTP GET)
        String gqlquery = new FileUtils().getGraphqlQuery(queryName);
        URI getURL = new GraphqlRequest().buildGETURI(gqlquery, variables);
        return getURL;
    }

    public SPARQLExtQuery parseSparqlGenerateQuery(String queryName) throws IOException {
        // Parse sparql-generate query
        String sparqlGenerateString = new FileUtils().getSparqlQuery(queryName);
        SPARQLExtQuery query = (SPARQLExtQuery) QueryFactory
                .create(sparqlGenerateString, SPARQLExt.SYNTAX);
        return query;
    }

    public Model executeGenerate(URI graphqlGETURL, SPARQLExtQuery query) {
        // replace source in sparql-generate (default.json) with GraphQL URI
        LocatorURLAccept locator = new LocatorURLAccept();
        LocationMapperAccept mapper = new LocationMapperAccept();
        mapper.addAltEntry("http://tib.eu/default.json", graphqlGETURL.toString());
        SPARQLExtStreamManager sm = SPARQLExtStreamManager.makeStreamManager(locator, mapper);

        // create the context
        Model model = ModelFactory.createDefaultModel();
        Context context = ContextUtils.build()
                .setInputModel(model)
                .setStreamManager(sm)
                .build();

        // create & execute the plan
        RootPlan plan = PlanFactory.create(query);
        Model output = plan.execGenerate(context);

        return output;
    }
}
