package com.medinet.infrastructure.repository;

import com.medinet.infrastructure.entity.InvoiceEntity;
import com.medinet.infrastructure.repository.jpa.InvoiceJpaRepository;
import com.medinet.integration.configuration.PersistenceContainerTestConfiguration;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PersistenceContainerTestConfiguration.class)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class InvoiceRepositoryTest {

    private InvoiceJpaRepository invoiceJpaRepository;

    @Test
    public void testFindByExistingUuidShouldReturnInvoice() {
        // given
        String uuid = "some-uuid";
        InvoiceEntity invoiceEntity = new InvoiceEntity();
        invoiceEntity.setUuid(uuid);
        invoiceEntity.setInvoiceId(1);
        invoiceEntity.setPdfData(new byte[1]);
        invoiceJpaRepository.save(invoiceEntity);

        // when
        Optional<InvoiceEntity> foundInvoice = invoiceJpaRepository.findByUuid(uuid);

        // Assert
        Assertions.assertTrue(foundInvoice.isPresent());
        Assertions.assertEquals(uuid, foundInvoice.get().getUuid());
    }

    @Test
    public void testFindByNonExistingUuidShouldReturnEmptyOptional() {
        // given
        String uuid = "non-existing-uuid";

        // when
        Optional<InvoiceEntity> foundInvoice = invoiceJpaRepository.findByUuid(uuid);

        // then
        Assertions.assertFalse(foundInvoice.isPresent());
    }

    @Test
    public void testExistsByExistingUuidShouldReturnTrue() {
        // Arrange
        String uuid = "some-uuid";
        InvoiceEntity invoiceEntity = new InvoiceEntity();
        invoiceEntity.setUuid(uuid);
        invoiceEntity.setInvoiceId(2);
        invoiceEntity.setPdfData(new byte[1]);
        invoiceJpaRepository.save(invoiceEntity);

        // Act
        boolean exists = invoiceJpaRepository.existsByUuid(uuid);

        // Assert
        Assertions.assertTrue(exists);
    }

    @Test
    public void testExistsByUuid_NonExistingUuid_ShouldReturnFalse() {
        // given
        String uuid = "non-existing-uuid";

        // when
        boolean exists = invoiceJpaRepository.existsByUuid(uuid);

        // then
        Assertions.assertFalse(exists);
    }
}
