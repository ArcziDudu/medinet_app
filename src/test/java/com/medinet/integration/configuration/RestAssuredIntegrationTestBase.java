package com.medinet.integration.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medinet.integration.support.ControllerTestSupport;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(PersistenceContainerTestConfiguration.class)
public abstract class RestAssuredIntegrationTestBase
        extends AbstractIT
        implements ControllerTestSupport {

    private String jSessionIdValue;

    @Autowired
    @SuppressWarnings("unused")
    private ObjectMapper objectMapper;

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Test
    void contextLoaded() {
        assertThat(true).isTrue();
    }


    public RequestSpecification requestSpecification() {
        return restAssuredBase()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .cookie("JSESSIONID", jSessionIdValue);
    }

    public RequestSpecification requestSpecificationNoAuthentication() {
        return restAssuredBase();
    }

    private RequestSpecification restAssuredBase() {
        return RestAssured
                .given()
                .config(getConfig())
                .basePath(basePath)
                .port(port);
    }

    private RestAssuredConfig getConfig() {
        return RestAssuredConfig.config()
                .objectMapperConfig(new ObjectMapperConfig()
                        .jackson2ObjectMapperFactory((type, s) -> objectMapper));
    }
}
