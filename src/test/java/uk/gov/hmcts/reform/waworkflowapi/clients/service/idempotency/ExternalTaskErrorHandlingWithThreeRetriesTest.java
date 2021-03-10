package uk.gov.hmcts.reform.waworkflowapi.clients.service.idempotency;

import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.waworkflowapi.exceptions.IdempotencyTaskWorkerException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalTaskErrorHandlingWithThreeRetriesTest {

    @Mock
    private ExternalTask externalTask;
    @Mock
    private ExternalTaskService externalTaskService;
    @Mock
    private Exception exception;

    private final ExternalTaskErrorHandlingWithThreeRetries externalTaskErrorHandlingWithThreeRetries =
        new ExternalTaskErrorHandlingWithThreeRetries();

    @BeforeEach
    void setUp() {
        when(externalTask.getId()).thenReturn("some external task id");
        when(exception.getMessage()).thenReturn("some exception message");
        when(exception.getCause()).thenReturn(new Throwable("some exception cause"));
    }

    @Test
    void given_retries_is_null_then_set_handle_failure() {
        given(externalTask.getRetries()).willReturn(null);

        externalTaskErrorHandlingWithThreeRetries.handleError(externalTask, externalTaskService, exception);

        verify(externalTaskService).handleFailure(
            "some external task id",
            "some exception message",
            "java.lang.Throwable: some exception cause",
            3,
            1000
        );
    }

    @Test
    void given_retries_is_greater_than_one_then_set_handle_failure() {
        given(externalTask.getRetries()).willReturn(3);

        externalTaskErrorHandlingWithThreeRetries.handleError(externalTask, externalTaskService, exception);

        verify(externalTaskService).handleFailure(
            "some external task id",
            "some exception message",
            "java.lang.Throwable: some exception cause",
            2,
            1000
        );
    }

    @Test
    void given_retries_is_not_greater_than_one_then_set_handle_failure() {
        given(externalTask.getRetries()).willReturn(1);

        IdempotencyTaskWorkerException actualException = Assertions.assertThrows(
            IdempotencyTaskWorkerException.class,
            () -> externalTaskErrorHandlingWithThreeRetries.handleError(externalTask, externalTaskService, exception)
        );

        assertThat(actualException.getMessage()).isEqualTo(String.format(
            "ERROR: Retrying three times could not fix the problem.%nThe task(%s) becomes an incident now.",
            externalTask.getId()
        ));

        verify(externalTaskService).handleFailure(
            "some external task id",
            "some exception message",
            "java.lang.Throwable: some exception cause",
            0,
            1000
        );
    }

}
