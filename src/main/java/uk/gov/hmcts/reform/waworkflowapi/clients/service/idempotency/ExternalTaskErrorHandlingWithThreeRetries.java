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
    public void handleError(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        if (externalTask.getRetries() == null) {
            setFailure(externalTask, externalTaskService, NUMBER_OF_RETRIES);
        } else if (externalTask.getRetries() > NO_MORE_RETRIES) {
            setFailure(externalTask, externalTaskService, externalTask.getRetries() - 1);
        } else {
            setFailure(externalTask, externalTaskService, INCIDENT_SIGNAL);
            throw new IdempotencyTaskWorkerException("*** ERROR with idempotency worker *** ");
        }
    }

    private void setFailure(ExternalTask externalTask, ExternalTaskService externalTaskService, int retries) {
        externalTaskService.handleFailure(
            externalTask.getId(),
            "david error message",
            "david error details",
            retries,
            1000
        );
    }

}
