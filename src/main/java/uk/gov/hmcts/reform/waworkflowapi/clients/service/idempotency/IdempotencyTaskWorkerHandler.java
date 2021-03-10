package uk.gov.hmcts.reform.waworkflowapi.clients.service.idempotency;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.idempotencykey.IdempotentId;
import uk.gov.hmcts.reform.waworkflowapi.exceptions.IdempotencyTaskWorkerException;

import java.util.Optional;

import static java.util.Collections.singletonMap;
import static uk.gov.hmcts.reform.waworkflowapi.clients.service.idempotency.IdempotencyTaskService.IS_DUPLICATE;

@Service
@Slf4j
public class IdempotencyTaskWorkerHandler {

    public static final int NUMBER_OF_RETRIES = 3;
    public static final int NO_MORE_RETRIES = 1;
    public static final int INCIDENT_SIGNAL = 0;

    private final IdempotencyTaskService idempotencyTaskService;

    public IdempotencyTaskWorkerHandler(
        IdempotencyTaskService idempotencyTaskService) {
        this.idempotencyTaskService = idempotencyTaskService;
    }

    public void checkIdempotency(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        log.info("checking idempotency...");
        try {
            Optional<IdempotentId> idempotentId = getIdempotentId(externalTask);
            idempotentId.ifPresentOrElse(
                id -> idempotencyTaskService.handleIdempotentIdProvidedScenario(externalTask, externalTaskService, id),
                () -> completeTask(externalTask, externalTaskService)
            );
        } catch (Exception e) {
            handleError(externalTask, externalTaskService);
        }
    }

    private void handleError(ExternalTask externalTask, ExternalTaskService externalTaskService) {
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

    private void completeTask(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        String msg = "No idempotencyKey found for process instance({}), "
            + "probably a service other than wa/ia is using the BPM.";
        log.info(msg, externalTask.getProcessInstanceId());
        externalTaskService.complete(externalTask, singletonMap(IS_DUPLICATE, false));
    }

    private Optional<IdempotentId> getIdempotentId(ExternalTask externalTask) {
        String idempotencyKey = externalTask.getVariable("idempotencyKey");
        if (StringUtils.isNotBlank(idempotencyKey)) {
            String tenantId = externalTask.getVariable("jurisdiction");
            log.info("build idempotentId with key({}) and tenantId({})...", idempotencyKey, tenantId);
            return Optional.of(new IdempotentId(idempotencyKey, tenantId));
        }
        log.info("idempotentId id blank");
        return Optional.empty();
    }

}
