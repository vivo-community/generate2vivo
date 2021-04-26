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
import fr.mines_stetienne.ci.sparql_generate.stream.LocatorFileAccept;
import fr.mines_stetienne.ci.sparql_generate.stream.LocatorURLAccept;
import fr.mines_stetienne.ci.sparql_generate.stream.SPARQLExtStreamManager;
import fr.mines_stetienne.ci.sparql_generate.utils.ContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.binding.BindingUtils;
import org.apache.jena.sparql.util.Context;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class GeneratePipeline {

    public Model run(String queryName, Map<String, String> input) {

        log.info("Read-in Generate Query for " + queryName);
        Try<SPARQLExtQuery> generateQuery = Try.apply(() -> parseSparqlGenerateQuery(queryName));
        generateQuery.failed().forEach(e -> {
            log.error("error while parsing Sparql Query: ",e);
            throw new SparqlParsingException(GeneratePipeline.class, queryName, input.toString());
        });
        log.info("Finished reading Generate Query");

        log.info("Run generate query");
        Try<Model> result = generateQuery.flatMap(generate ->
                Try.apply(() -> executeGenerate(input, generate)));
        result.failed().forEach(e -> {
            log.error("error while executing Sparql Query: ",e);
            throw new SparqlExecutionException(GeneratePipeline.class, queryName, input.toString());
        });
        log.info("Finished running generate query");

        return result.get();
    }

    public SPARQLExtQuery parseSparqlGenerateQuery(String queryName) throws IOException {
        // Parse sparql-generate query
        String sparqlGenerateString = new FileUtils().getSparqlQuery(queryName);
        SPARQLExtQuery query = (SPARQLExtQuery) QueryFactory
                .create(sparqlGenerateString, SPARQLExt.SYNTAX);
        return query;
    }

    public Model executeGenerate(Map<String, String> input, SPARQLExtQuery query) {
        // replace source in sparql-generate (default.json) with GraphQL URI
        LocatorFileAccept locator = new LocatorFileAccept(new File("resources").toURI().getPath());
        LocationMapperAccept mapper = new LocationMapperAccept();
        mapper.addAltEntry("https://projects.tib.eu/tapir/graphql/orga2person.graphql", "graphql/ror/orga2person.graphql");
        mapper.addAltEntry("https://projects.tib.eu/tapir/graphql/orga2person.rqg", "sparql/ror/orga2person.rqg");

        SPARQLExtStreamManager sm = SPARQLExtStreamManager.makeStreamManager(locator, mapper);

        // create the context
        Model model = ModelFactory.createDefaultModel();
        Context context = ContextUtils.build()
                .setInputModel(model)
                .setStreamManager(sm)
                .build();

        // transfer initial ror parameter into query via binding
        QuerySolutionMap initialBinding = new QuerySolutionMap();
        input.forEach((k, v) -> {
            RDFNode literal = model.createLiteral(v);
            initialBinding.add(k, literal);
        });
        List<Binding> bindings = Collections.singletonList(BindingUtils.asBinding(initialBinding));

        // create & execute the plan
        RootPlan plan = PlanFactory.create(query);
        Model output = plan.execGenerate(bindings, context);

        return output;
    }
}
