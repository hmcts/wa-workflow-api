package uk.gov.hmcts.reform.waworkflowapi.service;

import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.hibernate.PessimisticLockException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.idempotentkey.IdempotentId;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.idempotentkey.IdempotentKeys;
import uk.gov.hmcts.reform.waworkflowapi.clients.service.IdempotentKeysJpaRepo;
import uk.gov.hmcts.reform.waworkflowapi.clients.service.IdempotentKeysRepository;
import uk.gov.hmcts.reform.waworkflowapi.clients.service.JdbcRepo;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
//@ActiveProfiles("integration")
class IdempotentKeysTest {

    @Autowired
    private IdempotentKeysRepository repository;
    private IdempotentKeys idempotentKeysWithRandomId;
    private IdempotentId randomIdempotentId;
    @Autowired
    private IdempotentKeysJpaRepo idempotentKeysJpaRepo;
    @Autowired
    private JdbcRepo jdbcRepo;

    @BeforeEach
    void setUp() {
        randomIdempotentId = new IdempotentId(
            UUID.randomUUID().toString(),
            "ia"
        );

        idempotentKeysWithRandomId = new IdempotentKeys(
            randomIdempotentId,
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    @Test
    void jpaTest() {
        repository.save(idempotentKeysWithRandomId);

        Thread user1 = new Thread(() -> {
            log.info("start user1-thread...");
            repository.findById(randomIdempotentId);
            Awaitility.await().timeout(15, TimeUnit.SECONDS);
            repository.save(new IdempotentKeys(
                randomIdempotentId,
                "updated",
                LocalDateTime.now(),
                LocalDateTime.now()
            ));
        }, "user1-thread");

        Thread user2 = new Thread(() -> {
            log.info("start user2-thread...");
            repository.findById(randomIdempotentId);
            repository.save(new IdempotentKeys(
                randomIdempotentId,
                "updated2",
                LocalDateTime.now(),
                LocalDateTime.now()
            ));
        }, "user2-thread");

        user1.start();
        Awaitility.await().timeout(2, TimeUnit.SECONDS);
        user2.start();

    }

    @Test
    void given_readQueryOnRow_then_anotherQueryOnSameRowThrowException() {
        repository.save(idempotentKeysWithRandomId);

        Thread query1 = new Thread(() -> {
            log.info("start query1-thread...");

            // set exclusive lock on row
            repository.findById(randomIdempotentId);

            // simulates isDuplicate processing time
            Awaitility.await().timeout(15, TimeUnit.SECONDS);

            // finally update the row
            repository.save(new IdempotentKeys(
                randomIdempotentId,
                "updated",
                LocalDateTime.now(),
                LocalDateTime.now()
            ));

        }, "query1-thread");

        Thread query2 = new Thread(() -> {
            log.info("start query2-thread...");

            Assertions.assertThrows(RuntimeException.class, () -> repository.findById(randomIdempotentId));

            Assertions.fail("we should not get to this line");

        }, "query2-thread");

        query1.start();
        // Allow some time to ensure query1 is executed first
        Awaitility.await().timeout(2, TimeUnit.SECONDS);
        query2.start();
        Awaitility.await().timeout(10, TimeUnit.SECONDS);

    }

}
