package uk.gov.hmcts.reform.waworkflowapi.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.idempotencykey.IdempotencyKeys;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.idempotencykey.IdempotentId;
import uk.gov.hmcts.reform.waworkflowapi.clients.service.ExternalTaskWorker;
import uk.gov.hmcts.reform.waworkflowapi.clients.service.idempotency.IdempotencyKeysRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
@ActiveProfiles("integration")
class IdempotencyKeysRepositoryTest {

    public static final String EXPECTED_EXCEPTION = "org.springframework.dao.PessimisticLockingFailureException";

    @Autowired
    private IdempotencyKeysRepository repository;
    private IdempotencyKeys idempotencyKeysWithRandomId;
    private IdempotentId randomIdempotentId;

    //because of the workers polling camunda at start-up
    @MockBean
    private ExternalTaskWorker externalTaskWorker;

    @BeforeEach
    void setUp() {
        randomIdempotentId = new IdempotentId(
            UUID.randomUUID().toString(),
            "ia"
        );

        idempotencyKeysWithRandomId = new IdempotencyKeys(
            randomIdempotentId.getIdempotencyKey(),
            randomIdempotentId.getTenantId(),
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    @Test
    void given_save_query_then_it_saves_successfully() {
        repository.save(idempotencyKeysWithRandomId);

        Optional<IdempotencyKeys> actualIdempotencyKeys = repository.findByIdempotencyKeyAndTenantId(
            randomIdempotentId.getIdempotencyKey(),
            randomIdempotentId.getTenantId()
        );

        assertThat(actualIdempotencyKeys.isPresent()).isTrue();
        IdempotencyKeys presentActualIdempotencyKeys = actualIdempotencyKeys.get();
        assertThat(presentActualIdempotencyKeys).isEqualTo(idempotencyKeysWithRandomId);
    }

    @Test
    void given_readQueryOnRow_then_anotherQueryOnSameRowThrowException() throws InterruptedException {
        repository.save(idempotencyKeysWithRandomId);

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<?> futureException = executorService.submit(this::reader1);
        executorService.execute(this::reader2);

        await()
            .ignoreExceptions()
            .pollInterval(1, TimeUnit.SECONDS)
            .atMost(15, TimeUnit.SECONDS)
            .until(() -> {

                ExecutionException exception = Assertions.assertThrows(ExecutionException.class, futureException::get);
                assertThat(exception.getMessage())
                    .startsWith(EXPECTED_EXCEPTION);

                return exception.getMessage().startsWith(EXPECTED_EXCEPTION);
            });

        executorService.shutdown();
        //noinspection ResultOfMethodCallIgnored
        executorService.awaitTermination(20, TimeUnit.SECONDS);
    }

    private void reader2() {
        // Allow some time to ensure the reader is executed first
        await().timeout(2, TimeUnit.SECONDS);
        log.info("start reader2...");
        repository.findByIdempotencyKeyAndTenantId(
            randomIdempotentId.getIdempotencyKey(),
            randomIdempotentId.getTenantId()
        );

    }

    private void reader1() {
        log.info("start reader1...");
        repository.findByIdempotencyKeyAndTenantId(
            randomIdempotentId.getIdempotencyKey(),
            randomIdempotentId.getTenantId()
        );
    }

}
