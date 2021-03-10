package uk.gov.hmcts.reform.waworkflowapi.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.idempotencykey.IdempotencyKeys;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.idempotencykey.IdempotentId;
import uk.gov.hmcts.reform.waworkflowapi.clients.service.idempotency.IdempotencyKeysRepository;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static uk.gov.hmcts.reform.waworkflowapi.SpringBootFunctionalBaseTest.FT_STANDARD_TIMEOUT_SECS;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
@ActiveProfiles("integration")
class IdempotencyKeysRepositoryTest {

    public static final String EXPECTED_EXCEPTION = "org.springframework.orm.jpa.JpaSystemException";
    @Autowired
    private IdempotencyKeysRepository repository;
    private IdempotencyKeys idempotencyKeysWithRandomId;
    private IdempotentId randomIdempotentId;

    @BeforeEach
    void setUp() {
        randomIdempotentId = new IdempotentId(
            UUID.randomUUID().toString(),
            "ia"
        );

        idempotencyKeysWithRandomId = new IdempotencyKeys(
            randomIdempotentId,
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    @Test
    void given_readQueryOnRow_then_anotherQueryOnSameRowThrowException() throws InterruptedException {
        repository.save(idempotencyKeysWithRandomId);

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.execute(this::reader);
        Future<?> futureException = executorService.submit(this::writer);

        await()
            .ignoreExceptions()
            .pollInterval(1, TimeUnit.SECONDS)
            .atMost(FT_STANDARD_TIMEOUT_SECS, TimeUnit.SECONDS)
            .until(() -> {

                ExecutionException exception = Assertions.assertThrows(ExecutionException.class, futureException::get);
                assertThat(exception.getMessage())
                    .startsWith(EXPECTED_EXCEPTION);

                return exception.getMessage().startsWith(EXPECTED_EXCEPTION);
            });

        executorService.shutdown();
        //noinspection ResultOfMethodCallIgnored
        executorService.awaitTermination(3, TimeUnit.MINUTES);
    }

    private void writer() {
        // Allow some time to ensure the reader is executed first
        await().timeout(2, TimeUnit.SECONDS);

        log.info("start read and write ops...");

        repository.findById(randomIdempotentId);
        repository.save(new IdempotencyKeys(
            randomIdempotentId,
            "should not update because of lock",
            LocalDateTime.now(),
            LocalDateTime.now()
        ));

    }

    private void reader() {
        log.info("start reader thread...");
        repository.findById(randomIdempotentId);
    }

}