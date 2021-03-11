package eu.tib.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

public class GraphqlRequest {

    public static final String DC_GRAPHQL_API_URL = "https://api.datacite.org/graphql";

    public JsonNode buildPOSTBody(String gqlQuery, Map variables) {
        ObjectMapper mapper = new ObjectMapper();

        // every graphql query via http post consists of three parts:
        // 1) query (mandatory) 2) operationname 3) variables
        // see https://graphql.org/learn/serving-over-http/
        ObjectNode root = mapper.createObjectNode();
        root.put("query", gqlQuery);

        JsonNode vars = mapper.convertValue(variables, JsonNode.class);
        root.set("variables", vars);

        return root;
    }

    public URI buildGETURI(String gqlQuery, Map variables) {
        return UriComponentsBuilder
                .fromHttpUrl(DC_GRAPHQL_API_URL)
                .queryParam("query", gqlQuery)
                .queryParam("variables", map2Json(variables))
                .build()
                .encode()
                .toUri();
    }

    private String map2Json(Map variables) {
        if (variables.isEmpty()) return "";
        else {
            try {
                return new ObjectMapper().writeValueAsString(variables);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return "";
            }
        }
    }
}
