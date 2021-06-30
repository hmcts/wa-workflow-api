package uk.gov.hmcts.reform.waworkflowapi.clients.service.handler;

import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.Warning;
import uk.gov.hmcts.reform.waworkflowapi.clients.model.WarningValues;

import java.util.Map;

@Component
public class WarningTaskWorkerHandler {

    public void completeWarningTaskService(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Map<?, ?> variables = externalTask.getAllVariables();

        externalTaskService.complete(externalTask, Map.of(
            "hasWarnings",
            true,
            "warningList",
            mapWarningValues(variables)
        ));
    }

    private String mapWarningValues(Map<?, ?> variables) {
        final String warningStr = (String) variables.get("warningList");
        WarningValues warningValues = new WarningValues(warningStr);

        final String warningCode = (String) variables.get("warningCode");
        final String warningText = (String) variables.get("warningText");

        if (warningCode != null && warningText != null) {
            Warning warning = new Warning(warningCode, warningText);
            warningValues.getValues().add(warning);
        }

        return warningValues.getValuesAsJson();
    }

}

