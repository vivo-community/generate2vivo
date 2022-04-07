package eu.tib.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis( RequestHandlerSelectors.basePackage( "eu.tib" ) )
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false)
                .pathMapping("/")
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("generate2vivo")
                .description("Extensible Data Ingest Tool for VIVO. Contains data sources like Datacite Commons, ORCID and ROR.")
                .version("1.2.2")
                .build();
    }
}
