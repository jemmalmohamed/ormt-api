package ma.org.ancfcc.pva.config.documentation;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(contact = @Contact(name = "Mohamed Jemmal", email = "jemmalmohamed@gmail.com"), description = "Documentation for PVA API", title = "PVA API - ANCFCC", version = "1.0"), servers = {
                @Server(description = "Local ENV", url = "http://localhost:8083") })

@Configuration
public class OpenApiConfig {

        @Bean
        GroupedOpenApi allApis() { // group all APIs with `user` in the path
                return GroupedOpenApi.builder()
                                .group("API")
                                .pathsToMatch("/**")
                                .displayName("ALL ENDPOINTS")
                                .build();
        }

        @Bean
        GroupedOpenApi authApis() { // group all APIs with `user` in the path
                return GroupedOpenApi.builder()
                                .group("Authentication")
                                .pathsToMatch("/**/auth/**")

                                .build();
        }

        @Bean
        GroupedOpenApi userApis() { // group all APIs with `user` in the path
                return GroupedOpenApi.builder().group("user").pathsToMatch("/**/users/**").build();
        }

        @Bean
        GroupedOpenApi missionApis() {
                return GroupedOpenApi.builder().group("Missions").pathsToMatch("/**/missions/**").build();
        }

        @Bean
        GroupedOpenApi organismeApis() {
                return GroupedOpenApi.builder().group("Organismes").pathsToMatch("/**/organismes/**").build();
        }

        @Bean
        GroupedOpenApi planActionApis() {
                return GroupedOpenApi.builder().group("Plan Actions").pathsToMatch("/**/planactions/**").build();
        }

        @Bean
        GroupedOpenApi capteursApis() {
                return GroupedOpenApi.builder().group("capteurs").pathsToMatch("/**/capteurs/**").build();
        }

}
