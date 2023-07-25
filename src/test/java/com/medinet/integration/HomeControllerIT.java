package com.medinet.integration;

import com.medinet.integration.configuration.AbstractIT;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;


@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HomeControllerIT extends AbstractIT {

    private final TestRestTemplate testRestTemplate;

    @Test
    void thatHomePageRequiredSigningIn() {
        String url = String.format("http://localhost:%s%s/login", port, basePath);

        String page = this.testRestTemplate.getForObject(url, String.class);
        Assertions.assertThat(page).contains("Logowanie - Medinet");
    }

    @Test
    void thatMechanicPageRequiredSigningIn() {
        String url = String.format("http://localhost:%s%s/register", port, basePath);

        String page = this.testRestTemplate.getForObject(url, String.class);
        Assertions.assertThat(page).contains("Rejestracja - Medinet");
    }
}
