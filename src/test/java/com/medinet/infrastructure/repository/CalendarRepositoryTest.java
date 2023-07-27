package com.medinet.infrastructure.repository;

import com.medinet.infrastructure.repository.jpa.CalendarJpaRepository;
import com.medinet.integration.configuration.PersistenceContainerTestConfiguration;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PersistenceContainerTestConfiguration.class)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@ExtendWith(MockitoExtension.class)
class CalendarRepositoryTest {
    private CalendarJpaRepository calendarJpaRepository;

}