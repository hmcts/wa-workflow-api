package uk.gov.hmcts.reform.waworkflowapi.clients.service;

import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import static java.util.Collections.singletonMap;

@Component
public class WarningTaskWorkerHandler {

    public void setTaskWarningFlag(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        externalTaskService.complete(externalTask, null, singletonMap("hasWarnings", true));
    }

}

