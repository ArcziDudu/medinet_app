package com.medinet.infrastructure.configuration;

import com.medinet.Application;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfiguration {
    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("default")
                .pathsToMatch("/**")
                .packagesToScan(Application.class.getPackageName())
                .build();
    }

    @Bean
    public OpenAPI springDocOpenApi() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Medinet application")
                        .contact(contact())
                        .version("1.0"));
    }

    private Contact contact() {
        return new Contact()
                .name("Medinet")
                .email("medinet.rezerwacje@gmail.com");
    }
}
