package eu.tib.output.vivo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Profile("vivo")
@ConfigurationProperties
@Configuration("vivoProperties")
public class VIVOProperties {
    @Value("${vivo.url}")
    private String url;
    @Value("${vivo.email}")
    private String email;
    @Value("${vivo.password}")
    private String password;
    @Value("${vivo.graph}")
    private String graph = "http://vitro.mannlib.cornell.edu/default/vitro-kb-2";

    public String getURL() {
        return url;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getGraph() {
        return graph;
    }
}
