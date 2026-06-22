package co.com.bancolombia.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger configuration.
 *
 * Endpoint specs (paths, parameters, request/response schemas) are declared via
 * {@code @RouterOperations} / {@code @RouterOperation} in
 * {@link co.com.bancolombia.api.RouterRest}, which is the idiomatic approach
 * for WebFlux functional routers with springdoc.
 *
 * Available endpoints:
 *   - Swagger UI:   http://localhost:8080/webjars/swagger-ui/index.html
 *   - OpenAPI JSON: http://localhost:8080/v3/api-docs
 *   - OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(localServer()))
                .tags(List.of(
                        new Tag().name("Wallets").description("Operations related to wallets"),
                        new Tag().name("Pocketes").description("Operations related to wallet pocketes"),
                        new Tag().name("Transactions").description("Operations related to transactions inside a pocket")
                ));
    }

    private Info apiInfo() {
        return new Info()
                .title("Wallet API")
                .summary("Reactive RESTful API for managing wallets")
                .description("API for managing wallets, their pocketes and the transactions in each pocket. "
                        + "Built with Spring Boot 4 + WebFlux (functional routing) following Clean Architecture.")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Pragma Software")
                        .url("https://pragma.com.co")
                        .email("info@pragma.com.co"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html"));
    }

    private Server localServer() {
        return new Server()
                .url("http://localhost:8080")
                .description("Local development server");
    }
}
