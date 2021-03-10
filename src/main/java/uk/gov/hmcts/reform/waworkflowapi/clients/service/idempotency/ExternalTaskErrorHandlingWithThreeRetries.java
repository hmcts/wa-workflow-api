package uk.gov.hmcts.reform.waworkflowapi.clients.service.idempotency;

import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.waworkflowapi.exceptions.IdempotencyTaskWorkerException;

@Component
public class ExternalTaskErrorHandlingWithThreeRetries implements ExternalTaskErrorHandling {

    public static final int NUMBER_OF_RETRIES = 3;
    public static final int NO_MORE_RETRIES = 1;
    public static final int INCIDENT_SIGNAL = 0;

    @Override
    public void handleError(ExternalTask externalTask, ExternalTaskService externalTaskService, Exception exception) {
        if (externalTask.getRetries() == null) {
            setFailure(externalTask, externalTaskService, NUMBER_OF_RETRIES, exception);
        } else if (externalTask.getRetries() > NO_MORE_RETRIES) {
            setFailure(externalTask, externalTaskService, externalTask.getRetries() - 1, exception);
        } else {
            setFailure(externalTask, externalTaskService, INCIDENT_SIGNAL, exception);
            throwException(externalTask);
        }
    }

    private void throwException(ExternalTask externalTask) {
        String message = String.format(
            "ERROR: Retrying three times could not fix the problem.%nThe task(%s) becomes an incident now.",
            externalTask.getId()
        );
        throw new IdempotencyTaskWorkerException(message);
    }

    private void setFailure(ExternalTask externalTask,
                            ExternalTaskService externalTaskService,
                            int retries,
                            Exception exception) {
        externalTaskService.handleFailure(
            externalTask.getId(),
            exception.getMessage(),
            exception.getCause().toString(),
            retries,
            1000
        );
    }

}
