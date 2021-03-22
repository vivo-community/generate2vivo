package eu.tib.store;

import com.lambdista.util.Try;
import eu.tib.error.VIVOExportException;
import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Repository;

import java.io.StringWriter;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class VIVOExport {

    //@ToDo Error Handling
    public String insertData(Model data, VIVOProperties vivo) {
        Map prefixMap = data.getNsPrefixMap();
        String prefixes = prefixes2String(prefixMap);
        StringWriter stringWriter = new StringWriter();
        data.write(stringWriter, "TURTLE");
        String dataRDF = stringWriter.toString();

        String triples = dataRDF.replaceAll("@prefix.*\\.", "");

        String query = prefixes +
                "\n" +
                "INSERT DATA { GRAPH <" + vivo.getGraph() + "> { \n" +
                triples +
                "}}";

        Try<String> response = Try.apply(() -> new SparqlRequest(vivo, query).send());
        if (response.isFailure()){
            System.out.println("error while exporting data to VIVO: "+ response.get());
            throw new VIVOExportException(VIVOExport.class, "id", "insertData",
                    "vivo", vivo.toString());}
        else return response.get();
    }

    private String prefixes2String(Map<String, String> prefixes) {
        return prefixes.entrySet()
                .stream()
                .map(e -> "PREFIX " + e.getKey() + ": <" + e.getValue() + ">")
                .collect(Collectors.joining("\n"));
    }
}
