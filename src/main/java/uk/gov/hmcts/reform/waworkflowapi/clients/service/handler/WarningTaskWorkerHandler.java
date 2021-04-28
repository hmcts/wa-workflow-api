package uk.gov.hmcts.reform.waworkflowapi.clients.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import static java.util.Collections.singletonMap;

@Component
@Slf4j
public class WarningTaskWorkerHandler {

    public void setTaskWarningFlag(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        var caseId = externalTask.getVariable("caseId");
        log.info("Setting warning for task(id={}, caseId={})...", externalTask.getId(), caseId);
        externalTaskService.complete(externalTask, singletonMap("hasWarnings", true));
        log.info("Warning completed successfully for task(id={}, caseId={})", externalTask.getId(), caseId);
    }
}

