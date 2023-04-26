package uk.gov.hmcts.reform.waworkflowapi.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.idempotencykey.IdempotencyKeys;
import uk.gov.hmcts.reform.waworkflowapi.clients.service.idempotency.IdempotencyTaskService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class WorkflowApiTestingControllerTest {
    public static final String IDEMPOTENCY_KEY = "123";
    public static final String JURISDICTION = "WA";
    public static final String PROCESS_ID = "123-45";
    public static final LocalDateTime DATE = LocalDateTime.now();

    @Mock
    IdempotencyTaskService idempotencyTaskService;

    @InjectMocks
    WorkflowApiTestingController controller;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(controller, "environment", "local");
    }

    @Test
    void post_messages_should_delegate_to_eventMessageReceiverService() {
        IdempotencyKeys idempotencyKeys = new IdempotencyKeys(
            IDEMPOTENCY_KEY, JURISDICTION, PROCESS_ID, DATE, DATE
        );
        doReturn(Optional.of(idempotencyKeys))
            .when(idempotencyTaskService)
            .findByIdempotencyKeyAndTenantId(IDEMPOTENCY_KEY, JURISDICTION);

        ResponseEntity<IdempotencyKeys> response = controller.checkIdempotencyKey(IDEMPOTENCY_KEY, JURISDICTION);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(idempotencyKeys);

    }

}
